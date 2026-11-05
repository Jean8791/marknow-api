#!/usr/bin/env bash
set -euo pipefail

API="${API:-http://localhost:9696}"
ADMIN_EMAIL="${ADMIN_EMAIL:-admin@marknow.com.br}"
ADMIN_SENHA="${ADMIN_SENHA:?Defina ADMIN_SENHA com a senha do super admin}"
DEMO_EMAIL="${DEMO_EMAIL:-demo@marknow.com}"
DEMO_SENHA="${DEMO_SENHA:-demo1234}"
PLANO_NOME="Profissional"

command -v jq >/dev/null || { echo "Precisa do jq instalado." >&2; exit 1; }

chamar() {
  local metodo="$1" caminho="$2" corpo="${3:-}"
  local args=(-sS -X "$metodo" "$API$caminho" -w '\n%{http_code}')
  [[ -n "${TOKEN:-}" ]] && args+=(-H "Authorization: Bearer $TOKEN")
  [[ -n "$corpo" ]] && args+=(-H 'Content-Type: application/json' -d "$corpo")
  local saida status
  saida=$(curl "${args[@]}")
  status=$(tail -n1 <<<"$saida")
  saida=$(sed '$d' <<<"$saida")
  if (( status >= 300 )); then
    echo "Falhou: $metodo $caminho ($status) $saida" >&2
    exit 1
  fi
  printf '%s' "$saida"
}

login() {
  curl -sS "$API/auth/login" -H 'Content-Type: application/json' \
    -d "$(jq -n --arg e "$1" --arg s "$2" '{email: $e, senha: $s}')" | jq -r '.token // empty'
}

TOKEN=$(login "$ADMIN_EMAIL" "$ADMIN_SENHA")
[[ -n "$TOKEN" ]] || { echo "Não consegui logar como $ADMIN_EMAIL em $API." >&2; exit 1; }

PLANO_ID=$(chamar GET "/plano?size=50&search=$PLANO_NOME" | jq -r --arg n "$PLANO_NOME" '.content[] | select(.nome == $n) | .oid' | head -n1)
if [[ -z "$PLANO_ID" ]]; then
  PLANO_ID=$(chamar POST /plano "$(jq -n --arg n "$PLANO_NOME" \
    '{nome: $n, limiteAgendamentos: 1000, valor: 89.90, possuiTrial: true, diasTrial: 30}')" | jq -r .oid)
  echo "Plano criado: $PLANO_NOME"
fi
chamar PUT "/plano/$PLANO_ID" "$(jq -n --arg o "$PLANO_ID" --arg n "$PLANO_NOME" \
  '{oid: $o, nome: $n, limiteAgendamentos: 1000, valor: 89.90, possuiTrial: true, diasTrial: 30, status: "ATIVO"}')" >/dev/null

if [[ -z "$(login "$DEMO_EMAIL" "$DEMO_SENHA")" ]]; then
  CODIGO=$(chamar POST /convite "$(jq -n --arg e "$DEMO_EMAIL" \
    '{descricao: "Conta de demonstração", emailDestinatario: $e, expiraEm: "2099-01-01T00:00:00Z", maxUsos: 1}')" | jq -r .codigo)
  TOKEN="" chamar POST /auth/cadastro "$(jq -n --arg c "$CODIGO" --arg p "$PLANO_ID" --arg e "$DEMO_EMAIL" --arg s "$DEMO_SENHA" '{
    codigo: $c, oidPlano: $p,
    prestador: {nome: "Studio Demonstração", tipoPessoa: "PJ", documento: "11444777000161", email: $e,
                dddTelefone: "11", telefone: "33334444", cep: "01001000", logradouro: "Praça da Sé",
                numero: "100", bairro: "Sé", cidade: "São Paulo", uf: "SP", complemento: ""},
    usuario: {nome: "Visitante Demo", tipoPessoa: "PF", email: $e, dddTelefone: "11",
              senha: $s, comfirmarSenha: $s, role: "ADMIN"}}')" >/dev/null
  echo "Prestador de demonstração criado: $DEMO_EMAIL"
fi

TOKEN=$(login "$DEMO_EMAIL" "$DEMO_SENHA")
[[ -n "$TOKEN" ]] || { echo "Não consegui logar como $DEMO_EMAIL." >&2; exit 1; }

if [[ "$(chamar GET /servico | jq length)" == "0" ]]; then
  for s in 'Corte de cabelo|30|45' 'Escova|60|60' 'Coloração|120|180' 'Manicure|60|35'; do
    IFS='|' read -r nome duracao valor <<<"$s"
    chamar POST /servico "$(jq -n --arg n "$nome" --argjson d "$duracao" --argjson v "$valor" \
      '{nome: $n, duracaoMinutos: $d, valor: $v}')" >/dev/null
  done
  echo "Serviços criados."
fi

janelas() {
  local dias="$1"; shift
  jq -n --argjson dias "$dias" --args '[ $dias[] as $d | $ARGS.positional[] | split("-") | {diaSemana: $d, horaInicio: .[0], horaFim: .[1]} ]' "$@"
}

if [[ "$(chamar GET /profissional | jq length)" == "0" ]]; then
  chamar POST /profissional "$(jq -n --argjson j "$(janelas '[1,2,3,4,5]' 09:00-12:00 13:00-18:00)" \
    '{nome: "Paula Ribeiro", dddTelefone: "11", telefone: "911112222", disponibilidades: $j}')" >/dev/null
  chamar POST /profissional "$(jq -n --argjson j "$(janelas '[2,3,4,5,6]' 10:00-14:00 15:00-19:00)" \
    '{nome: "Rafael Torres", dddTelefone: "11", telefone: "933334444", disponibilidades: $j}')" >/dev/null
  chamar POST /profissional "$(jq -n --argjson j "$(janelas '[1,3,5]' 08:00-12:00)" \
    '{nome: "Juliana Castro", disponibilidades: $j}')" >/dev/null
  echo "Profissionais criados."
fi

echo "Pronto. Conta demo: $DEMO_EMAIL / $DEMO_SENHA"
echo "A agenda da demo é preenchida pela API (DemoAgendaJob) quando MARKNOW_DEMO_EMAIL=$DEMO_EMAIL estiver configurado."

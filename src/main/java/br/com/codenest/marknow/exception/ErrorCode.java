package br.com.codenest.marknow.exception;

public enum ErrorCode {

    // 400 - request inválido (bean validation / JSON malformado / parâmetros inválidos)
    BAD_REQUEST(400),

    // 404 - recurso não encontrado
    NOT_FOUND(404),

    //Acesso não autorizado
    UNAUTHORIZED(401),

    // 403 - acesso negado
    FORBIDDEN(403),

    // 409 - conflito de estado (concorrência, já existe, já reservado, etc.)
    CONFLICT(409),

    // 422 - não processável por regra/estado do domínio (request ok, mas regra impede)
    UNPROCESSABLE(422),

    // 500 - erro inesperado (fallback)
    INTERNAL_ERROR(500);

    private final int httpStatus;

    ErrorCode(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}

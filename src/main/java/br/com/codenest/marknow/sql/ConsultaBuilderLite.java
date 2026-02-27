package br.com.codenest.marknow.sql;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ConsultaBuilderLite {

    protected final EntityManager em;

    protected final StringBuilder sqlSelect = new StringBuilder();
    protected final StringBuilder sqlFrom = new StringBuilder();
    protected final StringBuilder sqlWhere = new StringBuilder();
    protected final StringBuilder sqlGroup = new StringBuilder();
    protected final StringBuilder sqlOrder = new StringBuilder();
    protected final StringBuilder sqlFinal = new StringBuilder();

    protected final Map<String, Object> params = new LinkedHashMap<>();

    protected Integer limit;
    protected Integer offset;

    public ConsultaBuilderLite(EntityManager em) {
        this.em = em;
    }

    public ConsultaBuilderLite select(String expr) {
        if (sqlSelect.indexOf("SELECT") >= 0) sqlSelect.append(", ");
        else sqlSelect.append("SELECT ");
        sqlSelect.append(expr);
        return this;
    }

    public ConsultaBuilderLite from(String expr) {
        if (sqlFrom.indexOf("FROM") >= 0) sqlFrom.append(" ");
        else sqlFrom.append(" FROM ");
        sqlFrom.append(expr);
        return this;
    }

    public ConsultaBuilderLite where(String expr) {
        if (sqlWhere.indexOf("WHERE") >= 0) sqlWhere.append(" AND ");
        else sqlWhere.append(" WHERE ");
        sqlWhere.append(expr);
        return this;
    }

    public ConsultaBuilderLite groupBy(String expr) {
        if (sqlGroup.indexOf("GROUP BY") >= 0) sqlGroup.append(", ");
        else sqlGroup.append(" GROUP BY ");
        sqlGroup.append(expr);
        return this;
    }

    /**
     * Não aceita qualquer coisa. Você passa a expressão já validada (whitelist fora daqui).
     */
    public ConsultaBuilderLite orderByRaw(String expr) {
        if (expr == null || expr.isBlank()) return this;
        if (sqlOrder.indexOf("ORDER BY") >= 0) sqlOrder.append(", ");
        else sqlOrder.append(" ORDER BY ");
        sqlOrder.append(expr);
        return this;
    }

    public ConsultaBuilderLite sqlFinal(String expr) {
        sqlFinal.append(" ").append(expr).append(" ");
        return this;
    }

    public ConsultaBuilderLite param(String name, Object value) {
        params.put(name, value);
        return this;
    }

    public ConsultaBuilderLite limit(int limit) {
        this.limit = limit;
        return this;
    }

    public ConsultaBuilderLite offset(int offset) {
        this.offset = offset;
        return this;
    }

    public String buildSql() {
        return new StringBuilder()
                .append(sqlSelect)
                .append(sqlFrom)
                .append(sqlWhere)
                .append(sqlGroup)
                .append(sqlOrder)
                .append(sqlFinal)
                .toString();
    }

    protected Query createNativeQuery(String sql) {
        Query q = em.createNativeQuery(sql);
        for (Map.Entry<String, Object> e : params.entrySet()) {
            q.setParameter(e.getKey(), e.getValue());
        }
        if (limit != null && limit > 0) q.setMaxResults(limit);
        if (offset != null && offset >= 0) q.setFirstResult(offset);
        return q;
    }

    /**
     * Útil pra count
     */
    protected Query createNativeQueryNoPage(String sql) {
        Query q = em.createNativeQuery(sql);
        for (Map.Entry<String, Object> e : params.entrySet()) {
            q.setParameter(e.getKey(), e.getValue());
        }
        return q;
    }

    public Set<String> paramNames() {
        return params.keySet();
    }
}
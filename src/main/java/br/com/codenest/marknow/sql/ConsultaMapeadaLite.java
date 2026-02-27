package br.com.codenest.marknow.sql;

import jakarta.persistence.EntityManager;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ConsultaMapeadaLite<T> extends ConsultaBuilderLite {

    private final Function<Object[], T> mapper;

    public ConsultaMapeadaLite(EntityManager em, Function<Object[], T> mapper) {
        super(em);
        this.mapper = mapper;
    }

    public List<T> executar() {
        String sql = buildSql();
        @SuppressWarnings("unchecked")
        List<Object[]> rows = createNativeQuery(sql).getResultList();
        if (rows == null || rows.isEmpty()) return Collections.emptyList();
        return rows.stream().map(mapper).toList();
    }

    public long count(String countSql) {
        Object r = createNativeQueryNoPage(countSql).getSingleResult();
        if (r instanceof Number n) return n.longValue();
        return Long.parseLong(String.valueOf(r));
    }
}
package br.com.codenest.marknow.util;

import br.com.codenest.marknow.dto.common.PageDTO;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import java.util.function.Function;
import java.util.stream.Collectors;

public class PaginationUtil {

    public static <E, R> PageDTO<R> toPageDTO(PanacheQuery<E> query, Function<E, R> mapper) {
        return new PageDTO<>(
                query.list().stream().map(mapper).collect(Collectors.toList()),
                query.count(),
                query.pageCount(),
                query.page().index,
                query.page().size
        );
    }
}
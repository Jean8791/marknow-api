package br.com.codenest.marknow.dto.common;

import java.util.List;

public record PageDTO<T>(
        List<T> content,
        long totalElements,
        int totalPages,
        int page,
        int size
) {
}
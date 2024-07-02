package br.org.hogwarts.utils.pagination;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;

import java.util.Map;

public class CustomPagination {

    private CustomPagination() {
        throw new IllegalStateException("Utility class");
    }

    public static HttpHeaders createPaginationHeaders(String acceptRanges, String contentRange, String totalPages) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Accept-Ranges", acceptRanges);
        httpHeaders.add("Content-Range", contentRange);
        httpHeaders.add("Total-Pages", totalPages);
        return httpHeaders;
    }

    public static Pageable createCustomPageable(Map<String, Object> params) {
        int page = 0;
        int size = 10;
        String sort = "id";
        String sortType = "asc";

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = ((String) entry.getValue()).trim();

            if (StringUtils.isBlank(value)) {
                continue;
            }
            switch (key) {
                case "page":
                    page = Integer.parseInt(value) - 1;
                    break;
                case "limit":
                    size = Integer.parseInt(value);
                    break;
                case "sort":
                    sort = value;
                    break;
                case "sortType":
                    sortType = value;
                    break;
                default:
                    break;
            }
        }

        return PageRequest.of(page, size, sort(sort, sortType));
    }

    private static Sort sort(String sort, String sortType) {
        return Sort.by("desc".equalsIgnoreCase(sortType) ? Sort.Direction.DESC : Sort.Direction.ASC, sort);
    }

}

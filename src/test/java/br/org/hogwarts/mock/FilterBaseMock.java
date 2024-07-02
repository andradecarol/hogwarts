package br.org.hogwarts.mock;

import java.util.HashMap;
import java.util.Map;

public class FilterBaseMock {

    private FilterBaseMock() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<String, Object> validParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("skip_size", "0");
        params.put("limit_size", "5");
        return params;
    }
}

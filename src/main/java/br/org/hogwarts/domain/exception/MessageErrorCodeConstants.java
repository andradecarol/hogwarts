package br.org.hogwarts.domain.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageErrorCodeConstants {

    //HTTP 400
    public static final String INVALID_REQUEST = "400.000";
    public static final String FIELD_NOT_BE_NULL = "400.001";
    public static final String FIELD_MUST_BE_MIN_MAX_CHARACTER = "400.002";
    public static final String FIELD_MUST_BE_LESS_THAN_VALUE = "400.003";
    public static final String FIELD_MUST_BE_GREATER_THAN_VALUE = "400.004";
    public static final String FIELD_MUST_BE_VALID = "400.014";
    public static final String ADDITIONAL_FIELDS_NOT_ALLOWED = "400.015";

    //HTTP 404
    public static final String NOT_FOUND = "404.000";
    public static final String MOTIVO_ID_NOT_FOUND = "404.001";

    //HTTP 422
    public static final String BUSINESS_ERROR = "422.000";

    public static final String NAME_HOUSE_ALREADY_EXISTS = "422.001";

    //HTTP 500
    public static final String INTERNAL_SERVER_ERROR = "500.000";

    public static final String UNEXPECTED_SERVER_ERROR = "500.001";

}
package br.org.hogwarts.domain.exception;

import org.springframework.http.HttpStatus;

public class MaxResultsPerPageException extends RestException {

    public MaxResultsPerPageException(String code, String message) {
        super(code, message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

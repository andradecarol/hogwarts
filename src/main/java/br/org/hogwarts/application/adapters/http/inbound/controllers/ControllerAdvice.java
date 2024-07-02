package br.org.hogwarts.application.adapters.http.inbound.controllers;

import br.org.hogwarts.application.adapters.http.inbound.controllers.dto.response.ErrorResponse;
import br.org.hogwarts.domain.exception.MaxResultsPerPageException;
import br.org.hogwarts.domain.exception.NotFoundException;
import br.org.hogwarts.domain.exception.RestException;
import br.org.hogwarts.domain.exception.UnprocessableEntityException;
import br.org.hogwarts.domain.exception.MessageErrorCodeConstants;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {

    private static final Integer JVM_MAX_STRING_LEN = Integer.MAX_VALUE;

    private final MessageSource messageSource;

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<List<ErrorResponse>> methodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonList(ErrorResponse.builder()
                .codigo(MessageErrorCodeConstants.FIELD_MUST_BE_VALID)
                .mensagem(getMessage(MessageErrorCodeConstants.FIELD_MUST_BE_VALID, e.getName()))
                .build()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Error> mediaTypeNotFoundException(final HttpMediaTypeNotSupportedException e) {
        return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<List<ErrorResponse>> assertionException(final HttpMessageNotReadableException e) {
        if (e.getCause() instanceof UnrecognizedPropertyException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonList(ErrorResponse.builder()
                            .codigo(MessageErrorCodeConstants.ADDITIONAL_FIELDS_NOT_ALLOWED)
                            .mensagem(getMessage(MessageErrorCodeConstants.ADDITIONAL_FIELDS_NOT_ALLOWED))
                            .build()));
        }
        if (e.getCause() instanceof JsonMappingException cause) {
            String field = cause.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("."));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonList(ErrorResponse.builder()
                            .codigo(MessageErrorCodeConstants.INVALID_REQUEST)
                            .mensagem(getMessage(MessageErrorCodeConstants.INVALID_REQUEST, field))
                            .build()));
        }
        return defaultBadRequestError();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<List<ErrorResponse>> missingServletRequestParameterException(
            final MissingServletRequestParameterException e) {
        return defaultBadRequestError();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException methodArgumentNotValidException) {
        List<ErrorResponse> messageErrors = Optional.ofNullable(methodArgumentNotValidException)
                .filter(argumentNotValidException -> !ObjectUtils.isEmpty(argumentNotValidException.getBindingResult()))
                .map(MethodArgumentNotValidException::getBindingResult)
                .filter(bindingResult -> !ObjectUtils.isEmpty(bindingResult.getAllErrors()))
                .map(BindingResult::getAllErrors)
                .stream()
                .flatMap(Collection::stream)
                .filter(objectError -> !ObjectUtils.isEmpty(objectError))
                .map(this::createError)
                .toList();
        return new ResponseEntity<>(messageErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ErrorResponse>> handleConstraintViolationException(
            ConstraintViolationException e) {
        List<ErrorResponse> errors = e.getConstraintViolations().stream()
                .map(constraint -> new ErrorResponse(constraint.getMessageTemplate(),
                        ((PathImpl) constraint.getPropertyPath()).getLeafNode(),
                        constraint.getConstraintDescriptor().getAttributes().get("value")))
                .toList();
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(RestException.class)
    public ResponseEntity<Object> handleRestException(RestException restException) {
        log.error(restException.getMessage(), restException);

        if (restException.getResponseBodyCode() != null) {
            return ResponseEntity
                    .status(restException.getStatus())
                    .body(new ErrorResponse(restException.getResponseBodyCode(), getMessage(restException.getResponseBodyCode())));
        }

        if (restException.getResponseBody() != null) {
            String message = StringUtils.isBlank(restException.getResponseBody().getMensagem())
                    ? getMessage(restException.getResponseBody().getCodigo())
                    : restException.getResponseBody().getMensagem();

            return ResponseEntity
                    .status(restException.getStatus())
                    .body(new ErrorResponse(restException.getResponseBody().getCodigo(), message));
        }

        return ResponseEntity.status(restException.getStatus()).build();
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<Object> handleUnprocessableEntityException(UnprocessableEntityException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(new ErrorResponse(exception.getResponseBody().getCodigo(), exception.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(new ErrorResponse(exception.getResponseBody().getCodigo(), exception.getMessage()));
    }

    @ExceptionHandler(MaxResultsPerPageException.class)
    public ResponseEntity<Object> handleMaxResultsPerPageException(MaxResultsPerPageException exception) {
        return ResponseEntity
                .status(exception.getStatus())
                .body(List.of(new ErrorResponse(exception.getResponseBody().getCodigo(), exception.getMessage())));
    }

    private String getMessage(String code, Object... args) {
        return this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    private ResponseEntity<List<ErrorResponse>> defaultBadRequestError() {
        return new ResponseEntity<>(
                Collections.singletonList(ErrorResponse.builder()
                        .codigo(MessageErrorCodeConstants.INVALID_REQUEST)
                        .mensagem(getMessage(MessageErrorCodeConstants.INVALID_REQUEST))
                        .build()),
                HttpStatus.BAD_REQUEST);
    }

    private ErrorResponse createError(ObjectError error) {
        String field = "";
        if (error instanceof FieldError fieldError) {
            field = fieldError.getField();
        }

        if (isMinOrMax(error)) {
            List<Object> argumentsList = Arrays.stream(Objects.requireNonNull(error.getArguments())).toList();
            return new ErrorResponse(error.getDefaultMessage(), field, argumentsList.get(1));
        }

        if (Objects.equals(error.getCode(), "Size")) {
            Integer min = null;
            Integer max = null;
            if (Objects.requireNonNull(error.getArguments()).length > 2) {
                List<Object> argumentsList = Arrays.stream(error.getArguments()).toList();

                Integer rawMax = (Integer) argumentsList.get(1);
                max = Objects.equals(rawMax, JVM_MAX_STRING_LEN) ? null : rawMax;

                Integer rawMin = (Integer) argumentsList.get(2);
                min = rawMin == 0 ? null : rawMin;
            }

            if (min != null && max != null) {
                return new ErrorResponse(error.getDefaultMessage(), field, min, max);
            } else if (min != null) {
                return new ErrorResponse(error.getDefaultMessage(), field, min);
            }

            return new ErrorResponse(error.getDefaultMessage(), field, max);
        }

        return ErrorResponse.builder()
                .codigo(error.getDefaultMessage())
                .mensagem(getMessage(error.getDefaultMessage(), field))
                .build();
    }

    private static boolean isMinOrMax(ObjectError error) {
        return Objects.equals(error.getCode(), "Max") || Objects.equals(error.getCode(), "Min");
    }
}
package br.org.hogwarts.application.adapters.http.inbound.controllers;

import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;


@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public abstract class AbstractControllerTest<T> {

    protected MockMvc mockMvc;

    protected MockHttpServletResponse response;

    protected ObjectWriter objectWriter;

    protected void thenExpectOkStatus() {
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    protected void thenExpectCreatedStatus() {
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    protected void thenExpectNoContentStatus() {
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    protected void thenExpectPartialContentStatus() {
        assertEquals(HttpStatus.PARTIAL_CONTENT.value(), response.getStatus());
    }

    protected void thenExpectBadRequestStatus() {
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    protected void thenExpectNotFoundStatus() {
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    protected void thenExpectUnprocessableEntityStatus() {
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    protected void thenExpectInternalServerErrorStatus() {
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
    }
}

package com.nutrike.core.config.handler

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException

class CustomThreadExceptionHandlerTest {
    private lateinit var exceptionHandler: CustomThreadExceptionHandler
    private lateinit var mockRequest: WebRequest

    @BeforeEach
    fun setUp() {
        exceptionHandler = CustomThreadExceptionHandler()
        mockRequest = mockk()
        every { mockRequest.getDescription(false) } returns "uri=/test-endpoint"
    }

    @Test
    fun `handleResponseStatusException should return correct response`() {
        val exception = ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found")
        val response = exceptionHandler.handleResponseStatusException(exception, mockRequest)

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Resource not found", response.body?.message)
        assertEquals("/test-endpoint", response.body?.target)
    }

    @Test
    fun `handleMethodArgumentNotValidException should return correct response`() {
        val mockMethodParam = mockk<MethodParameter>()

        val bindingResult: BindingResult = BeanPropertyBindingResult("objectName", "fieldName")
        bindingResult.addError(ObjectError("objectName", "Validation failed"))

        val exception = MethodArgumentNotValidException(mockMethodParam, bindingResult)
        val response = exceptionHandler.handleMethodArgumentNotValidException(exception, mockRequest)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("[Validation failed]", response.body?.message)
        assertEquals("/test-endpoint", response.body?.target)
    }

    @Test
    fun `handleHttpMessageNotReadableException should return correct response`() {
        val mockHttpInputMessage = mockk<HttpInputMessage>()

        val exception = HttpMessageNotReadableException("Malformed JSON request", mockHttpInputMessage)
        val response = exceptionHandler.handleHttpMessageNotReadableException(exception, mockRequest)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Malformed JSON request", response.body?.message)
        assertEquals("/test-endpoint", response.body?.target)
    }

    @Test
    fun `handleException should return internal server error response`() {
        val exception = Exception("Unexpected error")
        val response = exceptionHandler.handleException(exception, mockRequest)

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertEquals("Unexpected error", response.body?.message)
        assertEquals("/test-endpoint", response.body?.target)
    }
}

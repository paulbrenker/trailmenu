package com.nutrike.core.config.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nutrike.core.dto.ErrorResponseDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import java.io.PrintWriter
import java.io.StringWriter

class CustomAccessDeniedHandlerTest {
    private lateinit var accessDeniedHandler: CustomAccessDeniedHandler
    private lateinit var mockRequest: HttpServletRequest
    private lateinit var mockResponse: HttpServletResponse
    private lateinit var mockWriter: PrintWriter
    private lateinit var stringWriter: StringWriter

    @BeforeEach
    fun setUp() {
        accessDeniedHandler = CustomAccessDeniedHandler()
        mockRequest = mockk()
        mockResponse = mockk(relaxed = true)
        stringWriter = StringWriter()
        mockWriter = PrintWriter(stringWriter)

        every { mockRequest.requestURI } returns "/restricted-resource"
        every { mockResponse.writer } returns mockWriter
    }

    @Test
    fun `handle should set response status and write error response`() {
        val exception = AccessDeniedException("Access Denied")
        accessDeniedHandler.handle(mockRequest, mockResponse, exception)

        verify { mockResponse.status = HttpStatus.FORBIDDEN.value() }
        verify { mockResponse.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE }

        val expectedResponse =
            jacksonObjectMapper().writeValueAsString(
                ErrorResponseDto(
                    status = HttpStatus.FORBIDDEN.value(),
                    code = HttpStatus.FORBIDDEN.toString(),
                    message = "You do not have permission to access this resource.",
                    target = "/restricted-resource",
                ),
            )

        mockWriter.flush()
        assert(stringWriter.toString().trim() == expectedResponse)
    }
}

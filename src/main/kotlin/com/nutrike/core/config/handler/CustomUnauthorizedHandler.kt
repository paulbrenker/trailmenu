package com.nutrike.core.config.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nutrike.core.dto.ErrorResponseDto
import com.nutrike.core.exception.InvalidTokenException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

object CustomUnauthorizedHandler {
    fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        e: InvalidTokenException,
    ) {
        response!!.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
        response.writer.write(
            jacksonObjectMapper().writeValueAsString(
                ErrorResponseDto(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.toString(),
                    e.message,
                    request!!.requestURI,
                ),
            ),
        )
    }
}

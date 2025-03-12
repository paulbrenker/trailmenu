package com.nutrike.core.config.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nutrike.core.dto.ErrorResponseDto
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        e: AccessDeniedException?,
    ) {
        response!!.status = HttpStatus.FORBIDDEN.value()
        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE

        response.writer.write(
            jacksonObjectMapper().writeValueAsString(
                ErrorResponseDto(
                    status = HttpStatus.FORBIDDEN.value(),
                    code = HttpStatus.FORBIDDEN.toString(),
                    message = "You do not have permission to access this resource.",
                    target = request!!.requestURI,
                ),
            ),
        )
    }
}

package com.nutrike.core.config.handler

import com.nutrike.core.dto.ErrorResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException

@ControllerAdvice
class CustomThreadExceptionHandler {
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(
        e: ResponseStatusException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponseDto> =
        ResponseEntity.status(e.statusCode).body(
            ErrorResponseDto(
                status = e.statusCode.value(),
                code = e.statusCode.toString(),
                message = e.reason.toString(),
                target = getTargetFromRequest(request),
            ),
        )

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponseDto> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponseDto(
                status = HttpStatus.BAD_REQUEST.value(),
                code = HttpStatus.BAD_REQUEST.toString(),
                message =
                    e.bindingResult.allErrors
                        .map { it.defaultMessage }
                        .toString(),
                target = getTargetFromRequest(request),
            ),
        )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        e: HttpMessageNotReadableException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponseDto> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponseDto(
                status = HttpStatus.BAD_REQUEST.value(),
                code = HttpStatus.BAD_REQUEST.toString(),
                message = e.message ?: "request could not be parsed",
                target = getTargetFromRequest(request),
            ),
        )

    @ExceptionHandler(Exception::class)
    fun handleException(
        e: Exception,
        request: WebRequest,
    ): ResponseEntity<ErrorResponseDto> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponseDto(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                code = HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                message = e.message ?: "failed to process request",
                target = getTargetFromRequest(request),
            ),
        )

    private fun getTargetFromRequest(request: WebRequest) =
        request
            .getDescription(false)
            .substringAfter("=")
}

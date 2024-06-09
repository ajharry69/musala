package com.example.musala

import jakarta.servlet.http.HttpServletRequest
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.mediatype.problem.Problem
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import java.net.URI
import java.time.Instant


@ControllerAdvice
object MusalaHttpExceptionHandler {
    @ResponseBody
    @ExceptionHandler(MusalaException::class)
    operator fun invoke(exception: MusalaException, request: HttpServletRequest): ResponseEntity<Problem> {
        val problem = Problem.create()
            .withStatus(exception.statusCode)
            .withProperties(
                buildMap {
                    put("timestamp", Instant.now())
                    put("errorCode", exception.errorCode)
                    putAll(exception.extraProperties)
                },
            )
            .withDetail(exception.localizedMessage)
            .withInstance(URI.create(request.requestURI))

        return ResponseEntity.status(exception.statusCode)
            .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
            .body(problem)
    }

    @ResponseBody
    @ExceptionHandler(AuthenticationException::class)
    operator fun invoke(exception: AuthenticationException, request: HttpServletRequest): ResponseEntity<Problem> {
        val problem = Problem.create()
            .withStatus(HttpStatus.UNAUTHORIZED)
            .withProperties(
                buildMap {
                    put("timestamp", Instant.now())
                    put("errorCode", "AUTHENTICATION_ERROR")
                },
            )
            .withDetail(exception.localizedMessage)
            .withInstance(URI.create(request.requestURI))

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
            .body(problem)
    }


    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException::class)
    operator fun invoke(
        exception: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<Problem> {
        val problem = Problem.create()
            .withStatus(HttpStatus.BAD_REQUEST)
            .withProperties(
                buildMap {
                    put("timestamp", Instant.now())
                    put("errorCode", "INVALID_REQUEST_ERROR")
                },
            )
            .withDetail(exception.localizedMessage)
            .withInstance(URI.create(request.requestURI))

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
            .body(problem)
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException::class)
    operator fun invoke(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): ResponseEntity<Problem> {
        val fieldErrors = exception.bindingResult.fieldErrors.groupBy {
            it.field
        }.mapValues { (_, value) ->
            value.map {
                FieldValidationError(
                    code = it.code,
                    message = it.defaultMessage,
                    rejectedValue = it.rejectedValue,
                )
            }
        }
        val problem = Problem.create()
            .withStatus(HttpStatus.BAD_REQUEST)
            .withProperties(
                buildMap {
                    put("timestamp", Instant.now())
                    put("errorCode", "VALIDATION_ERROR")
                    put("fieldErrors", fieldErrors)
                },
            )
            .withDetail("Invalid data")
            .withInstance(URI.create(request.requestURI))

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
            .body(problem)
    }
}

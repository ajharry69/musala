package com.example.musala

import jakarta.servlet.ServletException
import org.springframework.http.HttpStatus

class MusalaException(
    val statusCode: HttpStatus,
    val errorCode: String,
    val extraProperties: Map<String, Any> = emptyMap(),
) : ServletException()
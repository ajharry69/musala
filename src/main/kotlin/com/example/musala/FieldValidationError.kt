package com.example.musala

data class FieldValidationError(
    val code: String? = null,
    val message: String? = null,
    val rejectedValue: Any? = null,
)
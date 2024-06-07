package com.example.musala.services.events.dtos

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.Range
import java.io.Serializable
import java.time.LocalDate

/**
 * DTO for {@link com.example.musala.services.events.dtos.EventEntity}
 */
data class EventApiRequest(
    @field:NotNull
    @field:NotEmpty
    @field:NotBlank
    @field:Length(max = 100)
    val name: String,
    @field:NotNull
    @field:Future
    val date: LocalDate,
    @field:Range(min = 1, max = 1000)
    val availableAttendeesCount: Int,
    @field:NotEmpty
    @field:NotBlank
    @field:Length(max = 500)
    val description: String,
    @field:NotNull
    val category: EventCategory,
) : Serializable
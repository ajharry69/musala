package com.example.musala.services.events.dtos

import java.io.Serializable
import java.time.LocalDate

/**
 * DTO for {@link com.example.musala.services.events.dtos.EventEntity}
 */
data class EventApiResponse(
    val id: Long,
    val name: String,
    val date: LocalDate,
    val availableAttendeesCount: Int,
    val description: String,
    val category: EventCategory,
) : Serializable
package com.example.musala.services.tickets.dtos

import jakarta.validation.constraints.Min
import java.io.Serializable

/**
 * DTO for {@link com.example.musala.services.tickets.dtos.TicketEntity}
 */
data class TicketApiResponse(
    val id: Long,
    @field:Min(1L)
    val attendeesCount: Int = 1,
) : Serializable

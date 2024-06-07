package com.example.musala.services.tickets.dtos

import jakarta.validation.constraints.Min
import java.io.Serializable

/**
 * DTO for {@link com.example.musala.services.tickets.dtos.TicketEntity}
 */
data class TicketApiRequest(
    @field:Min(1L)
    val attendeesCount: Int,
) : Serializable
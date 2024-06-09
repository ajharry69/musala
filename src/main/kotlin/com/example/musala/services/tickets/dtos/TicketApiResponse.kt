package com.example.musala.services.tickets.dtos

import com.example.musala.services.tickets.TicketStatus
import java.io.Serializable

/**
 * DTO for {@link com.example.musala.services.tickets.dtos.TicketEntity}
 */
data class TicketApiResponse(
    val id: Long,
    val attendeesCount: Int,
    val status: TicketStatus,
) : Serializable

package com.example.musala.services.tickets.services

import com.example.musala.services.tickets.dtos.TicketApiRequest
import com.example.musala.services.tickets.dtos.TicketApiResponse

interface TicketService {
    fun reserveTicket(eventId: Long, request: TicketApiRequest): TicketApiResponse
    fun findById(eventId: Long, ticketId: Long): TicketApiResponse
    fun findAll(eventId: Long): List<TicketApiResponse>
}

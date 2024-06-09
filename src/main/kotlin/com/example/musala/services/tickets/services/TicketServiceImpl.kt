package com.example.musala.services.tickets.services

import com.example.musala.MusalaException
import com.example.musala.services.events.dtos.EventEntity
import com.example.musala.services.events.repositories.EventRepository
import com.example.musala.services.tickets.TicketStatus
import com.example.musala.services.tickets.dtos.TicketApiRequest
import com.example.musala.services.tickets.dtos.TicketApiResponse
import com.example.musala.services.tickets.dtos.toApiResponse
import com.example.musala.services.tickets.dtos.toEntity
import com.example.musala.services.tickets.repositories.TicketRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TicketServiceImpl(
    private val repository: TicketRepository,
    private val eventRepository: EventRepository,
) : TicketService {
    @Transactional
    override fun reserveTicket(eventId: Long, request: TicketApiRequest): TicketApiResponse {
        val event = eventRepository.findForUpdateById(id = eventId)
            ?: throw MusalaException(HttpStatus.NOT_FOUND, errorCode = "EVENT_NOT_FOUND")

        if (event.availableAttendeesCount == 0) {
            throw MusalaException(HttpStatus.PRECONDITION_FAILED, errorCode = "EVENT_FULLY_BOOKED")
        }

        if (request.attendeesCount > event.availableAttendeesCount) {
            throw MusalaException(HttpStatus.PRECONDITION_FAILED, errorCode = "TOO_MANY_ATTENDEES")
        }

        event.availableAttendeesCount -= request.attendeesCount
        eventRepository.save(event)

        val entity = request.toEntity().apply {
            this.event = EventEntity(
                id = event.id,
                name = event.name,
                date = event.date,
                category = event.category,
                description = event.description,
                availableAttendeesCount = event.availableAttendeesCount,
            )
        }
        return repository.save(entity).toApiResponse()
    }

    @Transactional
    override fun cancelTicket(eventId: Long, ticketId: Long) {
        val event = eventRepository.findForUpdateById(id = eventId)
            ?: throw MusalaException(HttpStatus.NOT_FOUND, errorCode = "EVENT_NOT_FOUND")

        val ticket = repository.findForUpdateById(id = ticketId)
            ?: throw MusalaException(HttpStatus.NOT_FOUND, errorCode = "TICKET_NOT_FOUND")

        if (event.id != ticket.event!!.id) {
            throw MusalaException(
                HttpStatus.PRECONDITION_FAILED,
                errorCode = "SUSPICIOUS_TICKET_CANCELLATION_OPERATION",
            )
        }

        event.availableAttendeesCount += ticket.attendeesCount
        eventRepository.save(event)

        ticket.status = TicketStatus.Cancelled
        repository.save(ticket)
    }

    override fun findById(eventId: Long, ticketId: Long): TicketApiResponse {
        return repository.findByEvent_IdAndId(
            eventId = eventId,
            ticketId = ticketId,
        )?.toApiResponse()
            ?: throw MusalaException(HttpStatus.NOT_FOUND, errorCode = "TICKET_NOT_FOUND")
    }

    override fun findAll(eventId: Long): List<TicketApiResponse> {
        return repository.findAllByEvent_IdOrderByDateReservedAsc(
            eventId = eventId,
        ).map { it.toApiResponse() }
    }
}
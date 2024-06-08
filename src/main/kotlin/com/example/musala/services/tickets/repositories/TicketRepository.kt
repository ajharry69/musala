package com.example.musala.services.tickets.repositories

import com.example.musala.services.tickets.dtos.TicketEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TicketRepository : JpaRepository<TicketEntity, Long> {

    fun findAllByEvent_IdOrderByDateCreatedAsc(eventId: Long): List<TicketEntity>


    fun findByEvent_IdAndId(eventId: Long, ticketId: Long): TicketEntity?
}
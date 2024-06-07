package com.example.musala.services.tickets.repositories

import com.example.musala.services.tickets.dtos.TicketEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface TicketRepository : JpaRepository<TicketEntity, Long>, JpaSpecificationExecutor<TicketEntity> {

    fun findAllByEvent_IdOrderByDateCreatedAsc(eventId: Long): List<TicketEntity>


    fun findByEvent_IdAndId(eventId: Long, ticketId: Long): TicketEntity?
}
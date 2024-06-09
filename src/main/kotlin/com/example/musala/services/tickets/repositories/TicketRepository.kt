package com.example.musala.services.tickets.repositories

import com.example.musala.services.tickets.dtos.TicketEntity
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.QueryHints
import java.time.LocalDate

interface TicketRepository : JpaRepository<TicketEntity, Long> {

    fun findAllByEvent_IdOrderByDateReservedAsc(eventId: Long): List<TicketEntity>

    fun findByEvent_IdAndId(eventId: Long, ticketId: Long): TicketEntity?

    @QueryHints(
        QueryHint(
            name = "jakarta.persistence.lock.timeout",
            value = "-2",
        ),
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findTop50ByNotifiedFalseAndEvent_DateBetween(dateStart: LocalDate, dateEnd: LocalDate): List<TicketEntity>
}
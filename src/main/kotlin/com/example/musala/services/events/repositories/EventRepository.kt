package com.example.musala.services.events.repositories

import com.example.musala.services.events.dtos.EventEntity
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.QueryHints

interface EventRepository : JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
    @QueryHints(
        QueryHint(
            name = "jakarta.persistence.lock.timeout",
            value = "-2",
        ),
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findForUpdateById(id: Long): EventEntity?
}
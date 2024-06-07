package com.example.musala.services.events.repositories

import com.example.musala.services.events.dtos.EventEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface EventRepository : JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {
    @Transactional
    @Modifying
    @Query("update EventEntity e set e.availableAttendeesCount = :availableAttendeesCount where e.id = :id")
    fun updateAvailableAttendeesCountById(
        @Param("availableAttendeesCount") availableAttendeesCount: Int,
        @Param("id") eventId: Long,
    ): Int
}
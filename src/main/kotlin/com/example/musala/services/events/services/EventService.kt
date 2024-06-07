package com.example.musala.services.events.services

import com.example.musala.services.events.EventFilters
import com.example.musala.services.events.dtos.EventApiRequest
import com.example.musala.services.events.dtos.EventApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface EventService {
    fun createEvent(request: EventApiRequest): EventApiResponse
    fun findById(eventId: Long): EventApiResponse
    fun findAll(pageable: Pageable, filters: EventFilters): Page<EventApiResponse>
    fun updateAvailableAttendeesCountById(eventId: Long, availableAttendeesCount: Int)
}

package com.example.musala.services.events.services

import com.example.musala.services.events.EventFilters
import com.example.musala.services.events.dtos.EventApiRequest
import com.example.musala.services.events.dtos.EventApiResponse

interface EventService {
    fun createEvent(request: EventApiRequest): EventApiResponse
    fun findById(eventId: Long): EventApiResponse
    fun findAll(filters: EventFilters): List<EventApiResponse>
    fun updateAvailableAttendeesCountById(eventId: Long, availableAttendeesCount: Int)
}

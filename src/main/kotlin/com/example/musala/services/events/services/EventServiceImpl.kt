package com.example.musala.services.events.services

import com.example.musala.MusalaException
import com.example.musala.services.events.EventFilters
import com.example.musala.services.events.dtos.EventApiRequest
import com.example.musala.services.events.dtos.EventApiResponse
import com.example.musala.services.events.dtos.toApiResponse
import com.example.musala.services.events.dtos.toEntity
import com.example.musala.services.events.repositories.EventRepository
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class EventServiceImpl(private val repository: EventRepository) : EventService {
    override fun createEvent(request: EventApiRequest): EventApiResponse {
        val entity = request.toEntity()
        return repository.save(entity).toApiResponse()
    }

    override fun findById(eventId: Long): EventApiResponse {
        return repository.findByIdOrNull(id = eventId)?.toApiResponse()
            ?: throw MusalaException(HttpStatus.NOT_FOUND, errorCode = "EVENT_NOT_FOUND")
    }

    override fun findAll(filters: EventFilters): List<EventApiResponse> {
        val specification = EventSpecification(filters = filters)
        val responses = repository.findAll(specification, Sort.by(Sort.Order.asc("date"))).map {
            it.toApiResponse()
        }

        if (responses.isEmpty()) {
            throw MusalaException(HttpStatus.NOT_FOUND, errorCode = "EMPTY_EVENTS")
        }

        return responses
    }
}
package com.example.musala.services.events

import com.example.musala.services.events.dtos.EventCategory
import java.time.LocalDate
import java.util.*

data class EventFilters(
    val query: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val category: EventCategory? = null,
    val reservedById: UUID? = null,
)

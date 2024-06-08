package com.example.musala.services.events

import com.example.musala.services.events.dtos.EventCategory
import java.time.LocalDate

data class EventFilters(
    val query: String? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val category: EventCategory? = null,
)

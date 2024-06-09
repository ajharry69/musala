package com.example.musala.services.events.services

import com.example.musala.MusalaException
import com.example.musala.services.events.EventFilters
import com.example.musala.services.events.dtos.EventApiRequest
import com.example.musala.services.events.dtos.EventCategory
import com.example.musala.services.events.dtos.EventEntity
import com.example.musala.services.events.repositories.EventRepository
import org.junit.jupiter.api.*
import org.mockito.Mockito.*
import org.mockito.kotlin.argumentCaptor
import org.springframework.http.HttpStatus
import java.time.LocalDate
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class EventServiceImplTest {
    @Test
    fun `create event`() {
        val repository = mock(EventRepository::class.java)
        val service = EventServiceImpl(repository = repository)
        `when`(repository.save(any()))
            .thenReturn(EventEntity(id = 1))
        val request = EventApiRequest(
            name = "Test",
            date = LocalDate.now(),
            availableAttendeesCount = 1,
            description = "Test",
            category = EventCategory.entries.random(),
        )

        val actual = service.createEvent(request = request)

        assertNotNull(actual.id)
    }

    @Nested
    @DisplayName("find by id")
    inner class FindById {
        @Test
        fun `for valid id`() {
            val repository = mock(EventRepository::class.java)
            val service = EventServiceImpl(repository = repository)
            `when`(repository.findById(any()))
                .thenReturn(Optional.ofNullable(EventEntity(id = 1)))

            val actual = service.findById(eventId = 1)

            assertNotNull(actual.id)
        }

        @Test
        fun `for invalid id`() {
            val repository = mock(EventRepository::class.java)
            val service = EventServiceImpl(repository = repository)
            `when`(repository.findById(any()))
                .thenReturn(Optional.ofNullable(null))

            val error = assertThrows<MusalaException> {
                service.findById(eventId = 1)
            }

            assertAll(
                { assertEquals("EVENT_NOT_FOUND", error.errorCode) },
                { assertEquals(HttpStatus.NOT_FOUND, error.statusCode) },
            )
        }
    }

    @Test
    fun `find all`() {
        val repository = mock(EventRepository::class.java)
        val service = EventServiceImpl(repository = repository)
        `when`(repository.findAll(any<EventSpecification>()))
            .thenReturn(listOf(EventEntity(id = 1)))

        val actual = service.findAll(EventFilters())

        assertAll(
            { assertEquals(1, actual.size) },
            {
                val specCapture = argumentCaptor<EventSpecification>()
                verify(repository)
                    .findAll(specCapture.capture())
            },
        )
    }
}
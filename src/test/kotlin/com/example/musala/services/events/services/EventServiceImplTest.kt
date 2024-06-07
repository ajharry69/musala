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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.*
import kotlin.test.assertContentEquals
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

            assertThrows<MusalaException> {
                service.findById(eventId = 1)
            }
        }
    }

    @Test
    fun `find all`() {
        val repository = mock(EventRepository::class.java)
        val service = EventServiceImpl(repository = repository)
        `when`(repository.findAll(any<EventSpecification>(), any<Pageable>()))
            .thenReturn(PageImpl(listOf(EventEntity(id = 1))))

        val actual = service.findAll(Pageable.unpaged(), EventFilters(null))

        assertAll(
            { assertEquals(1, actual.size) },
            { assertEquals(1, actual.totalPages) },
            {
                val specCapture = argumentCaptor<EventSpecification>()
                val pageableCapture = argumentCaptor<Pageable>()
                verify(repository)
                    .findAll(specCapture.capture(), pageableCapture.capture())
            },
        )
    }

    @Test
    fun `update available attendees count by id`() {
        val repository = mock(EventRepository::class.java)
        val service = EventServiceImpl(repository = repository)

        service.updateAvailableAttendeesCountById(
            eventId = 1,
            availableAttendeesCount = 20,
        )

        val availableAttendeesCountCapture = argumentCaptor<Int>()
        val eventIdCapture = argumentCaptor<Long>()
        verify(repository).updateAvailableAttendeesCountById(
            availableAttendeesCountCapture.capture(),
            eventIdCapture.capture(),
        )

        assertAll(
            { assertContentEquals(listOf(1), eventIdCapture.allValues) },
            { assertContentEquals(listOf(20), availableAttendeesCountCapture.allValues) },
        )
    }
}
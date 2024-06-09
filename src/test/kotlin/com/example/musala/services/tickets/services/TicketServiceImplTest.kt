package com.example.musala.services.tickets.services

import com.example.musala.MusalaException
import com.example.musala.services.events.dtos.EventCategory
import com.example.musala.services.events.dtos.EventEntity
import com.example.musala.services.events.repositories.EventRepository
import com.example.musala.services.tickets.TicketStatus
import com.example.musala.services.tickets.dtos.TicketApiRequest
import com.example.musala.services.tickets.dtos.TicketEntity
import com.example.musala.services.tickets.repositories.TicketRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito.*
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TicketServiceImplTest {

    @Nested
    @DisplayName("reserve ticket")
    inner class ReverseTicket {
        @Test
        fun `for an in-existing event`() {
            val eventRepository = mock(EventRepository::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventRepository = eventRepository,
            )

            `when`(eventRepository.findForUpdateById(anyLong()))
                .thenReturn(null)
            val request = TicketApiRequest(attendeesCount = 1)

            val error = assertThrows<MusalaException> {
                service.reserveTicket(eventId = 3, request = request)
            }

            assertAll(
                { assertEquals("EVENT_NOT_FOUND", error.errorCode) },
                { assertEquals(HttpStatus.NOT_FOUND, error.statusCode) },
            )
        }

        @ParameterizedTest
        @CsvSource(
            "2,20,18",
            "20,20,0",
        )
        fun `when attendee count is within available event attendee count`(
            attendeesCount: Int,
            availableAttendeesCount: Int,
            newAvailableAttendeesCount: Int,
        ) {
            val eventRepository = mock(EventRepository::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventRepository = eventRepository,
            )

            `when`(repository.save(any()))
                .thenReturn(TicketEntity(id = 1))
            val eventEntity = EventEntity(
                id = 3,
                name = "Test",
                description = "Test",
                date = LocalDate.now(),
                availableAttendeesCount = availableAttendeesCount,
                category = EventCategory.entries.random(),
            )
            `when`(eventRepository.findForUpdateById(anyLong()))
                .thenReturn(eventEntity)
            val request = TicketApiRequest(attendeesCount = attendeesCount)

            val actual = service.reserveTicket(eventId = 3, request = request)

            assertAll(
                { assertNotNull(actual.id) },
                {
                    val eventCapture = argumentCaptor<EventEntity>()
                    verify(eventRepository).save(
                        eventCapture.capture(),
                    )

                    assertAll(
                        { assertContentEquals(listOf(3), eventCapture.allValues.map { it.id }) },
                        {
                            assertContentEquals(
                                listOf(newAvailableAttendeesCount),
                                eventCapture.allValues.map { it.availableAttendeesCount },
                            )
                        },
                    )
                },
            )
        }

        @Test
        fun `update available attendees count happens despite ticket reservation failure`() {
            val eventRepository = mock(EventRepository::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventRepository = eventRepository,
            )

            `when`(repository.save(any()))
                .thenThrow(RuntimeException::class.java)
            val eventEntity = EventEntity(
                id = 3,
                name = "Test",
                description = "Test",
                date = LocalDate.now(),
                availableAttendeesCount = 20,
                category = EventCategory.entries.random(),
            )
            `when`(eventRepository.findForUpdateById(anyLong()))
                .thenReturn(eventEntity)
            val request = TicketApiRequest(attendeesCount = 2)

            assertThrows<RuntimeException> {
                service.reserveTicket(eventId = 3, request = request)
            }

            assertAll(
                {
                    val entityCapture = argumentCaptor<TicketEntity>()
                    verify(repository).save(
                        entityCapture.capture(),
                    )

                    assertAll(
                        {
                            assertContentEquals(
                                listOf(null),
                                entityCapture.allValues.map { it.id },
                            )
                        },
                        {
                            assertContentEquals(
                                listOf(2),
                                entityCapture.allValues.map { it.attendeesCount },
                            )
                        },
                        {
                            assertEquals(
                                1,
                                entityCapture.allValues.mapNotNull { it.event }.size,
                            )
                        },
                    )
                },
                {
                    val eventCapture = argumentCaptor<EventEntity>()
                    verify(eventRepository).save(
                        eventCapture.capture(),
                    )

                    assertAll(
                        { assertContentEquals(listOf(3), eventCapture.allValues.map { it.id }) },
                        {
                            assertContentEquals(
                                listOf(18),
                                eventCapture.allValues.map { it.availableAttendeesCount },
                            )
                        },
                    )
                },
            )
        }

        @ParameterizedTest
        @CsvSource(
            "0,EVENT_FULLY_BOOKED",
            "10,TOO_MANY_ATTENDEES",
        )
        fun `when attendee count is above available event attendee count`(
            availableAttendeesCount: Int,
            expectedErrorCode: String,
        ) {
            val eventRepository = mock(EventRepository::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventRepository = eventRepository,
            )

            `when`(eventRepository.findForUpdateById(anyLong())).thenReturn(
                EventEntity(
                    id = 1,
                    name = "Test",
                    description = "Test",
                    date = LocalDate.now(),
                    availableAttendeesCount = availableAttendeesCount,
                    category = EventCategory.entries.random(),
                )
            )
            val request = TicketApiRequest(attendeesCount = 11)

            val error = assertThrows<MusalaException> {
                service.reserveTicket(eventId = 3, request = request)
            }

            assertAll(
                { assertEquals(expectedErrorCode, error.errorCode) },
                { assertEquals(HttpStatus.PRECONDITION_FAILED, error.statusCode) },
                {
                    val eventCapture = argumentCaptor<EventEntity>()
                    verify(eventRepository, never()).save(
                        eventCapture.capture(),
                    )
                },
                {
                    val entityCapture = argumentCaptor<TicketEntity>()
                    verify(repository, never()).save(
                        entityCapture.capture()
                    )
                },
            )
        }
    }

    @Nested
    @DisplayName("cancel ticket")
    inner class CancelTicket {
        @Test
        fun `for an in-existing event`() {
            val eventRepository = mock(EventRepository::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventRepository = eventRepository,
            )

            `when`(eventRepository.findForUpdateById(anyLong()))
                .thenReturn(null)

            val error = assertThrows<MusalaException> {
                service.cancelTicket(eventId = 3, ticketId = 1)
            }

            assertAll(
                { assertEquals("EVENT_NOT_FOUND", error.errorCode) },
                { assertEquals(HttpStatus.NOT_FOUND, error.statusCode) },
            )
        }

        @Test
        fun `for an in-existing ticket`() {
            val eventRepository = mock(EventRepository::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventRepository = eventRepository,
            )

            `when`(eventRepository.findForUpdateById(anyLong())).thenReturn(
                EventEntity(
                    id = 3,
                    name = "Test",
                    description = "Test",
                    date = LocalDate.now(),
                    category = EventCategory.entries.random(),
                )
            )

            `when`(repository.findForUpdateById(anyLong()))
                .thenReturn(null)

            val error = assertThrows<MusalaException> {
                service.cancelTicket(eventId = 3, ticketId = 1)
            }

            assertAll(
                { assertEquals("TICKET_NOT_FOUND", error.errorCode) },
                { assertEquals(HttpStatus.NOT_FOUND, error.statusCode) },
            )
        }


        @Test
        fun `for mismatched ticket and event ids`() {
            val eventRepository = mock(EventRepository::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventRepository = eventRepository,
            )

            `when`(eventRepository.findForUpdateById(anyLong())).thenReturn(
                EventEntity(
                    id = 3,
                    name = "Test",
                    description = "Test",
                    date = LocalDate.now(),
                    category = EventCategory.entries.random(),
                )
            )

            `when`(repository.findForUpdateById(anyLong()))
                .thenReturn(TicketEntity(id = 1, event = EventEntity(id = 4)))

            val error = assertThrows<MusalaException> {
                service.cancelTicket(eventId = 3, ticketId = 1)
            }

            assertAll(
                { assertEquals("SUSPICIOUS_TICKET_CANCELLATION_OPERATION", error.errorCode) },
                { assertEquals(HttpStatus.PRECONDITION_FAILED, error.statusCode) },
            )
        }

        @ParameterizedTest
        @CsvSource(
            "2,18,20",
            "20,0,20",
        )
        fun `when attendee count is within available event attendee count`(
            attendeesCount: Int,
            availableAttendeesCount: Int,
            newAvailableAttendeesCount: Int,
        ) {
            val eventRepository = mock(EventRepository::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventRepository = eventRepository,
            )

            val eventEntity = EventEntity(
                id = 3,
                name = "Test",
                description = "Test",
                date = LocalDate.now(),
                availableAttendeesCount = availableAttendeesCount,
                category = EventCategory.entries.random(),
            )
            `when`(eventRepository.findForUpdateById(anyLong()))
                .thenReturn(eventEntity)
            `when`(repository.findForUpdateById(anyLong()))
                .thenReturn(TicketEntity(id = 1, attendeesCount = attendeesCount, event = eventEntity))

            service.cancelTicket(eventId = 3, ticketId = 1)

            assertAll(
                {
                    val eventCapture = argumentCaptor<EventEntity>()
                    verify(eventRepository)
                        .save(eventCapture.capture())

                    assertAll(
                        { assertContentEquals(listOf(3), eventCapture.allValues.map { it.id }) },
                        {
                            assertContentEquals(
                                listOf(newAvailableAttendeesCount),
                                eventCapture.allValues.map { it.availableAttendeesCount },
                            )
                        },
                    )
                },
                {
                    val ticketCapture = argumentCaptor<TicketEntity>()
                    verify(repository)
                        .save(ticketCapture.capture())

                    assertAll(
                        { assertContentEquals(listOf(1), ticketCapture.allValues.map { it.id }) },
                        {
                            assertContentEquals(
                                listOf(attendeesCount),
                                ticketCapture.allValues.map { it.attendeesCount },
                            )
                        },
                        {
                            assertContentEquals(
                                listOf(TicketStatus.Cancelled),
                                ticketCapture.allValues.map { it.status },
                            )
                        },
                    )
                },
            )
        }

        @Test
        fun `update available attendees count happens despite ticket cancellation failure`() {
            val eventRepository = mock(EventRepository::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventRepository = eventRepository,
            )

            `when`(repository.save(any()))
                .thenThrow(RuntimeException::class.java)

            val eventEntity = EventEntity(
                id = 3,
                name = "Test",
                description = "Test",
                date = LocalDate.now(),
                availableAttendeesCount = 20,
                category = EventCategory.entries.random(),
            )
            `when`(eventRepository.findForUpdateById(anyLong()))
                .thenReturn(eventEntity)
            `when`(repository.findForUpdateById(anyLong()))
                .thenReturn(TicketEntity(id = 1, attendeesCount = 2, event = eventEntity))

            assertThrows<RuntimeException> {
                service.cancelTicket(eventId = 3, ticketId = 1)
            }

            assertAll(
                {
                    val entityCapture = argumentCaptor<TicketEntity>()
                    verify(repository).save(
                        entityCapture.capture(),
                    )

                    assertAll(
                        {
                            assertContentEquals(
                                listOf(1),
                                entityCapture.allValues.map { it.id },
                            )
                        },
                        {
                            assertContentEquals(
                                listOf(2),
                                entityCapture.allValues.map { it.attendeesCount },
                            )
                        },
                        {
                            assertContentEquals(
                                listOf(TicketStatus.Cancelled),
                                entityCapture.allValues.map { it.status },
                            )
                        },
                        {
                            assertEquals(
                                1,
                                entityCapture.allValues.mapNotNull { it.event }.size,
                            )
                        },
                    )
                },
                {
                    val eventCapture = argumentCaptor<EventEntity>()
                    verify(eventRepository).save(
                        eventCapture.capture(),
                    )

                    assertAll(
                        { assertContentEquals(listOf(3), eventCapture.allValues.map { it.id }) },
                        {
                            assertContentEquals(
                                listOf(22),
                                eventCapture.allValues.map { it.availableAttendeesCount },
                            )
                        },
                    )
                },
            )
        }
    }

    @Nested
    @DisplayName("find by id")
    inner class FindById {
        @Test
        fun `for valid id`() {
            val eventRepository = mock(EventRepository::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventRepository = eventRepository,
            )
            `when`(repository.findByEvent_IdAndId(anyLong(), anyLong()))
                .thenReturn(TicketEntity(id = 1))

            val actual = service.findById(eventId = 3, ticketId = 1)

            assertNotNull(actual.id)
        }

        @Test
        fun `for invalid id`() {
            val eventRepository = mock(EventRepository::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventRepository = eventRepository,
            )
            `when`(repository.findByEvent_IdAndId(anyLong(), anyLong()))
                .thenReturn(null)

            val error = assertThrows<MusalaException> {
                service.findById(eventId = 3, ticketId = 1)
            }

            assertAll(
                { assertEquals("TICKET_NOT_FOUND", error.errorCode) },
                { assertEquals(HttpStatus.NOT_FOUND, error.statusCode) },
            )
        }
    }

    @Test
    fun `find all`() {
        val eventRepository = mock(EventRepository::class.java)
        val repository = mock(TicketRepository::class.java)
        val service = TicketServiceImpl(
            repository = repository,
            eventRepository = eventRepository,
        )
        `when`(repository.findAllByEvent_IdOrderByDateReservedAsc(eventId = 1))
            .thenReturn(listOf(TicketEntity(id = 1)))

        val actual = service.findAll(eventId = 1)

        assertAll(
            { assertEquals(1, actual.size) },
            {
                val eventIdCapture = argumentCaptor<Long>()
                verify(repository)
                    .findAllByEvent_IdOrderByDateReservedAsc(eventIdCapture.capture())

                assertContentEquals(listOf(1), eventIdCapture.allValues)
            },
        )
    }
}
package com.example.musala.services.tickets.services

import com.example.musala.MusalaException
import com.example.musala.services.events.dtos.EventApiResponse
import com.example.musala.services.events.dtos.EventCategory
import com.example.musala.services.events.services.EventService
import com.example.musala.services.tickets.dtos.TicketApiRequest
import com.example.musala.services.tickets.dtos.TicketEntity
import com.example.musala.services.tickets.repositories.TicketRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito.*
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import java.time.LocalDate
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TicketServiceImplTest {

    @Nested
    @DisplayName("reserve ticket")
    inner class ReverseTicket {
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
            val eventService = mock(EventService::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventService = eventService,
            )

            `when`(repository.save(any()))
                .thenReturn(TicketEntity(id = 1))
            `when`(eventService.findById(anyLong())).thenReturn(
                EventApiResponse(
                    id = 1,
                    name = "Test",
                    description = "Test",
                    date = LocalDate.now(),
                    availableAttendeesCount = availableAttendeesCount,
                    category = EventCategory.entries.random(),
                )
            )
            val request = TicketApiRequest(attendeesCount = attendeesCount)

            val actual = service.reserveTicket(eventId = 3, request = request)

            assertAll(
                { assertNotNull(actual.id) },
                {
                    val eventIdCapture = argumentCaptor<Long>()
                    val availableAttendeesCountCapture = argumentCaptor<Int>()
                    verify(eventService).updateAvailableAttendeesCountById(
                        eventIdCapture.capture(),
                        availableAttendeesCountCapture.capture()
                    )

                    assertContentEquals(listOf(3), eventIdCapture.allValues)
                    assertContentEquals(listOf(newAvailableAttendeesCount), availableAttendeesCountCapture.allValues)
                },
            )
        }

        @Test
        fun `update available attendees count happens despite ticket reservation failure`() {
            val eventService = mock(EventService::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventService = eventService,
            )

            `when`(repository.save(any()))
                .thenThrow(RuntimeException::class.java)
            `when`(eventService.findById(anyLong())).thenReturn(
                EventApiResponse(
                    id = 1,
                    name = "Test",
                    description = "Test",
                    date = LocalDate.now(),
                    availableAttendeesCount = 20,
                    category = EventCategory.entries.random(),
                )
            )
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
                    val eventIdCapture = argumentCaptor<Long>()
                    val availableAttendeesCountCapture = argumentCaptor<Int>()
                    verify(eventService).updateAvailableAttendeesCountById(
                        eventIdCapture.capture(),
                        availableAttendeesCountCapture.capture()
                    )
                },
            )
        }

        @Test
        fun `when attendee count is above available event attendee count`() {
            val eventService = mock(EventService::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventService = eventService,
            )

            `when`(eventService.findById(anyLong())).thenReturn(
                EventApiResponse(
                    id = 1,
                    name = "Test",
                    description = "Test",
                    date = LocalDate.now(),
                    availableAttendeesCount = 10,
                    category = EventCategory.entries.random(),
                )
            )
            val request = TicketApiRequest(attendeesCount = 11)

            assertThrows<MusalaException> {
                service.reserveTicket(eventId = 3, request = request)
            }

            assertAll(
                {
                    val eventIdCapture = argumentCaptor<Long>()
                    val availableAttendeesCountCapture = argumentCaptor<Int>()
                    verify(eventService, never()).updateAvailableAttendeesCountById(
                        eventIdCapture.capture(),
                        availableAttendeesCountCapture.capture()
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
    @DisplayName("find by id")
    inner class FindById {
        @Test
        fun `for valid id`() {
            val eventService = mock(EventService::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventService = eventService,
            )
            `when`(repository.findByEvent_IdAndId(anyLong(), anyLong()))
                .thenReturn(TicketEntity(id = 1))

            val actual = service.findById(eventId = 3, ticketId = 1)

            assertNotNull(actual.id)
        }

        @Test
        fun `for invalid id`() {
            val eventService = mock(EventService::class.java)
            val repository = mock(TicketRepository::class.java)
            val service = TicketServiceImpl(
                repository = repository,
                eventService = eventService,
            )
            `when`(repository.findByEvent_IdAndId(anyLong(), anyLong()))
                .thenReturn(null)

            assertThrows<MusalaException> {
                service.findById(eventId = 3, ticketId = 1)
            }
        }
    }

    @Test
    fun `find all`() {
        val eventService = mock(EventService::class.java)
        val repository = mock(TicketRepository::class.java)
        val service = TicketServiceImpl(
            repository = repository,
            eventService = eventService,
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
package com.example.musala.services.tickets


import com.example.musala.BaseIntegrationTest
import com.example.musala.services.events.dtos.EventCategory
import com.example.musala.services.events.dtos.EventEntity
import com.example.musala.services.events.repositories.EventRepository
import com.example.musala.services.tickets.dtos.TicketEntity
import com.example.musala.services.tickets.repositories.TicketRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import java.time.LocalDate


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TicketControllerTest(
    @Autowired private val repository: TicketRepository,
    @Autowired private val eventRepository: EventRepository,
) : BaseIntegrationTest() {
    lateinit var event: EventEntity

    @BeforeEach
    fun setUp() {
        RestAssured.port = RestAssured.DEFAULT_PORT

//        repository.deleteAll()
        eventRepository.deleteAll()

        event = eventRepository.save(
            EventEntity(
                name = "Musala Engineering",
                description = "Test description for Musala Engineering event.",
                date = LocalDate.now().plusMonths(5),
                category = EventCategory.Conference,
                availableAttendeesCount = 100,
            )
        )
        repository.save(
            TicketEntity(
                attendeesCount = 10,
                event = event,
            )
        )

        repository.save(
            TicketEntity(
                attendeesCount = 50,
                event = event,
            )
        )

        repository.save(
            TicketEntity(
                attendeesCount = 2,
                event = event,
            )
        )
    }

    @Test
    fun `find all`() {
        given()
            /*.auth().preemptive().oauth2(getAccessToken())*/
            .get("/events/${event.id}/tickets")
            .apply { prettyPrint() }
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", equalTo(3))
    }

    @Nested
    @DisplayName("GET - /events/{eventId}/tickets/{ticketId}")
    inner class FindTicketById {
        @Test
        fun `with valid event id`() {
            val ticket = repository.save(
                TicketEntity(
                    attendeesCount = 7,
                    event = event,
                )
            )

            given()
                /*.auth().preemptive().oauth2(getAccessToken())*/
                .get("/events/${event.id}/tickets/${ticket.id}")
                .apply { prettyPrint() }
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", greaterThan(0))
                .body("attendeesCount", greaterThan(0))
                .body("_links.tickets.href", allOf(startsWith("http"), endsWith("/tickets")))
                .body("_links.self.href", allOf(startsWith("http")))
        }

        @Test
        fun `with invalid event id`() {
            given()
                /*.auth().preemptive().oauth2(getAccessToken())*/
                .get("/events/111111111111111/tickets/1234")
                .apply { prettyPrint() }
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errorCode", equalTo("TICKET_NOT_FOUND"))
        }

        @Test
        fun `with valid event id but invalid ticket id`() {
            given()
                /*.auth().preemptive().oauth2(getAccessToken())*/
                .get("/events/${event.id}/tickets/111111111111111")
                .apply { prettyPrint() }
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errorCode", equalTo("TICKET_NOT_FOUND"))
        }
    }

    @Nested
    @DisplayName("POST - /events/{eventId}/tickets")
    inner class CreateTicket {
        @Test
        fun `with valid request body`() {
            given()
                /*.auth().preemptive().oauth2(getAccessToken())*/
                .contentType(ContentType.JSON)
                .body("""{"attendeesCount": 2}""")
                .post("/events/${event.id}/tickets")
                .apply { prettyPrint() }
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", greaterThan(0))
                .body("attendeesCount", equalTo(2))
                .body("_links.tickets.href", allOf(startsWith("http"), endsWith("/tickets")))
                .body("_links.self.href", allOf(startsWith("http")))
                .header(HttpHeaders.LOCATION, allOf(startsWith("http")))

            assertThat(repository.count())
                .isEqualTo(4)
        }

        @ParameterizedTest
        @CsvSource(
            "-1,400,VALIDATION_ERROR",
            "0,400,VALIDATION_ERROR",
            "1001,412,EVENT_FULLY_BOOKED",
        )
        fun `with invalid request body`(attendeesCount: Int, expectedStatusCode: Int, expectedErrorCode: String) {
            given()
                /*.auth().preemptive().oauth2(getAccessToken())*/
                .contentType(ContentType.JSON)
                .body("""{"attendeesCount": $attendeesCount}""")
                .post("/events/${event.id}/tickets")
                .apply { prettyPrint() }
                .then()
                .statusCode(expectedStatusCode)
                .body("errorCode", equalTo(expectedErrorCode))

            assertThat(repository.count())
                .isEqualTo(3)
        }
    }
}
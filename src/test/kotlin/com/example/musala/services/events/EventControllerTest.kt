package com.example.musala.services.events

import com.example.musala.BaseIntegrationTest
import com.example.musala.services.events.dtos.EventApiRequest
import com.example.musala.services.events.dtos.EventCategory
import com.example.musala.services.events.dtos.EventEntity
import com.example.musala.services.events.repositories.EventRepository
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import java.time.LocalDate
import java.util.stream.Stream


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class EventControllerTest(
    @Autowired private val repository: EventRepository,
) : BaseIntegrationTest() {

    @BeforeEach
    fun setUp() {
        RestAssured.port = RestAssured.DEFAULT_PORT

        repository.deleteAll()

        repository.save(
            EventEntity(
                name = "Musala Engineering",
                description = "Test description for Musala Engineering event.",
                date = LocalDate.now().plusMonths(5),
                category = EventCategory.Conference,
            )
        )

        repository.save(
            EventEntity(
                name = "DevOps at Musala",
                description = "A description of Musala Developer Operations event.",
                date = LocalDate.now().plusYears(1),
                category = EventCategory.Conference,
            )
        )

        repository.save(
            EventEntity(
                name = "Musala Engineering Division Bowling",
                description = "Musala Engineering Division Bowling description.",
                date = LocalDate.now().plusWeeks(3),
                category = EventCategory.Game,
            )
        )
    }

    @ParameterizedTest
    @CsvSource(
        """,3""",
        """dev,1""",
        """operations,1""",
        """engineer,2""",
        """enGinEEr,2""",
    )
    fun `find all`(query: String?, expectedCount: Int) {
        given()
            /*.auth().preemptive().oauth2(getAccessToken())*/
            .queryParams(mapOf("query" to query))
            .get("/events")
            .apply { prettyPrint() }
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("size()", equalTo(expectedCount))
    }

    @Nested
    @DisplayName("GET - /events/{eventId}")
    inner class FindEventById {
        @Test
        fun `with valid event id`() {
            val event = repository.save(
                EventEntity(
                    name = "Musala Engineering",
                    description = "Test description for Musala Engineering event.",
                    date = LocalDate.now().plusMonths(5),
                    category = EventCategory.Conference,
                )
            )

            given()
                /*.auth().preemptive().oauth2(getAccessToken())*/
                .get("/events/${event.id}")
                .apply { prettyPrint() }
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", greaterThan(0))
                .body("name", notNullValue())
                .body("category", notNullValue())
                .body("date", notNullValue())
                .body("description", notNullValue())
                .body("availableAttendeesCount", greaterThan(0))
                .body("_links.events.href", allOf(startsWith("http"), endsWith("/events{?query}")))
                .body("_links.tickets.href", allOf(startsWith("http"), endsWith("/tickets")))
                .body("_links.reserveTicket.href", allOf(startsWith("http"), endsWith("/tickets")))
                .body("_links.self.href", allOf(startsWith("http")))
        }

        @Test
        fun `with invalid event id`() {
            given()
                /*.auth().preemptive().oauth2(getAccessToken())*/
                .get("/events/111111111111111")
                .apply { prettyPrint() }
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("errorCode", equalTo("EVENT_NOT_FOUND"))
        }
    }

    @Nested
    @DisplayName("POST - /events")
    inner class CreateEvent {
        @ParameterizedTest
        @EnumSource(value = EventCategory::class)
        fun `with valid request body`(category: EventCategory) {
            val eventDate = LocalDate.now().plusWeeks(1).toString()
            given()
                /*.auth().preemptive().oauth2(getAccessToken())*/
                .contentType(ContentType.JSON)
                .body(
                    """{
                      "name": "Test",
                      "date": "$eventDate",
                      "availableAttendeesCount": 100,
                      "description": "Test description",
                      "category": "$category"
                    }""".trimIndent(),
                )
                .post("/events")
                .apply { prettyPrint() }
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", greaterThan(0))
                .body("name", equalTo("Test"))
                .body("category", equalTo("$category"))
                .body("date", equalTo(eventDate))
                .body("description", equalTo("Test description"))
                .body("availableAttendeesCount", equalTo(100))
                .body("_links.events.href", allOf(startsWith("http"), endsWith("/events{?query}")))
                .body("_links.tickets.href", allOf(startsWith("http"), endsWith("/tickets")))
                .body("_links.reserveTicket.href", allOf(startsWith("http"), endsWith("/tickets")))
                .body("_links.self.href", allOf(startsWith("http")))
                .header(HttpHeaders.LOCATION, allOf(startsWith("http")))

            assertThat(repository.count())
                .isEqualTo(4)
        }

        @ParameterizedTest
        @ArgumentsSource(InvalidEventApiRequestProvider::class)
        fun `with invalid request body`(data: Pair<EventApiRequest, Int>) {
            val (request, numberOfFieldValidationErrors) = data
            given()
                /*.auth().preemptive().oauth2(getAccessToken())*/
                .contentType(ContentType.JSON)
                .body(request)
                .post("/events")
                .apply { prettyPrint() }
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("errorCode", equalTo("VALIDATION_ERROR"))
                .body("fieldErrors.size()", equalTo(numberOfFieldValidationErrors))

            assertThat(repository.count())
                .isEqualTo(3)
        }
    }

    object InvalidEventApiRequestProvider : ArgumentsProvider {
        override fun provideArguments(context: ExtensionContext): Stream<out Arguments?> {
            return Stream.of(
                EventApiRequest(
                    name = "Test",
                    date = LocalDate.now().minusDays(1),
                    availableAttendeesCount = 1,
                    description = "Test",
                    category = EventCategory.entries.random(),
                ) to 1,
                EventApiRequest(
                    name = "    ",
                    date = LocalDate.now().minusDays(1),
                    availableAttendeesCount = 1_001,
                    description = "   ",
                    category = EventCategory.entries.random(),
                ) to 4,
                EventApiRequest(
                    name = "",
                    date = LocalDate.now().minusDays(1),
                    availableAttendeesCount = 1_001,
                    description = "",
                    category = EventCategory.entries.random(),
                ) to 4,
                EventApiRequest(
                    name = "N".repeat(101),
                    date = LocalDate.now().minusDays(1),
                    availableAttendeesCount = 1_001,
                    description = "D".repeat(501),
                    category = EventCategory.entries.random(),
                ) to 4,
                EventApiRequest(
                    name = "N".repeat(100),
                    date = LocalDate.now().minusDays(1),
                    availableAttendeesCount = 1_000,
                    description = "D".repeat(500),
                    category = EventCategory.entries.random(),
                ) to 1,
                EventApiRequest(
                    name = "N".repeat(100),
                    date = LocalDate.now().plusDays(1),
                    availableAttendeesCount = -1,
                    description = "D".repeat(500),
                    category = EventCategory.entries.random(),
                ) to 1,
                EventApiRequest(
                    name = "N".repeat(100),
                    date = LocalDate.now().minusDays(1),
                    availableAttendeesCount = 1,
                    description = "D".repeat(500),
                    category = EventCategory.entries.random(),
                ) to 1,
            ).map(Arguments::of)
        }
    }
}
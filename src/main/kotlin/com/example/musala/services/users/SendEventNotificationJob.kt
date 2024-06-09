package com.example.musala.services.users

import com.example.musala.services.tickets.repositories.TicketRepository
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDate
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@Component
class SendEventNotificationJob(
    private val repository: TicketRepository,
    private val transactionTemplate: TransactionTemplate,
) {
    @Scheduled(cron = "\${application.jobs.send-event-notification.cron:0 0 9 * * ?}")
    fun invoke() {
        val dateStart = LocalDate.now()
        val dateEnd = dateStart.plusDays(1)

        val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

        var pending = true
        while (pending) {
            pending = transactionTemplate.execute {
                val tickets = repository.findTop50ByNotifiedFalseAndEvent_DateBetween(
                    dateStart = dateStart,
                    dateEnd = dateEnd,
                )
                if (tickets.isEmpty()) return@execute false

                tickets.forEach { ticket ->
                    coroutineScope.launch {
                        val fcmDelay = Random.nextLong(50, 5_000).milliseconds
                        logger.info("Waiting {} for FCM response...", fcmDelay)
                        delay(fcmDelay)

                        ticket.notified = true
                        repository.save(ticket)
                    }
                }
                true
            } ?: false
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SendEventNotificationJob::class.java.simpleName)
    }
}
package com.example.musala.audit

import org.springframework.data.auditing.DateTimeProvider
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.temporal.TemporalAccessor
import java.util.*


@Component(value = "dateTimeProvider")
class MusalaDateTimeProvider : DateTimeProvider {
    override fun getNow(): Optional<TemporalAccessor> {
        return Optional.of(OffsetDateTime.now())
    }
}
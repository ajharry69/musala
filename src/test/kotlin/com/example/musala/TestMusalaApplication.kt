package com.example.musala

import org.springframework.boot.devtools.restart.RestartScope
import org.springframework.boot.fromApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.boot.with
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer

@TestConfiguration(proxyBeanMethods = false)
class TestMusalaApplication {

    @Bean
    @RestartScope
    @ServiceConnection
    fun postgresContainer(): PostgreSQLContainer<*> {
        return Containers.POSTGRESQL_CONTAINER
    }

}

fun main(args: Array<String>) {
    fromApplication<MusalaApplication>().with(TestMusalaApplication::class).run(*args)
}

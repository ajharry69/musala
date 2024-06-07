package com.example.musala

import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

object Containers {
    val POSTGRESQL_CONTAINER: PostgreSQLContainer<*> =
        PostgreSQLContainer(DockerImageName.parse("postgres:16.3-alpine"))
}
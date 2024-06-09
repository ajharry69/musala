package com.example.musala

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class MusalaApplication

fun main(args: Array<String>) {
    runApplication<MusalaApplication>(*args)
}

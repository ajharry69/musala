package com.example.musala.services.tickets.dtos

fun TicketEntity.toApiResponse(): TicketApiResponse {
    return TicketApiResponse(
        id = id!!,
        status = status,
        attendeesCount = attendeesCount,
    )
}

fun TicketApiRequest.toEntity(): TicketEntity {
    return TicketEntity(attendeesCount = attendeesCount)
}
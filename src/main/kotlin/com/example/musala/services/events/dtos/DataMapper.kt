package com.example.musala.services.events.dtos

fun EventEntity.toApiResponse(): EventApiResponse {
    return EventApiResponse(
        id = id!!,
        name = name,
        date = date,
        category = category,
        description = description,
        availableAttendeesCount = availableAttendeesCount,
    )
}

fun EventApiRequest.toEntity(): EventEntity {
    return EventEntity(
        name = name,
        date = date,
        category = category,
        description = description,
        availableAttendeesCount = availableAttendeesCount,
    )
}
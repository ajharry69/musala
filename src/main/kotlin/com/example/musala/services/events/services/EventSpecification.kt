package com.example.musala.services.events.services

import com.example.musala.services.events.EventFilters
import com.example.musala.services.events.dtos.EventCategory
import com.example.musala.services.events.dtos.EventEntity
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

class EventSpecification(private val filters: EventFilters) : Specification<EventEntity> {
    override fun toPredicate(
        root: Root<EventEntity>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        val predicates = buildList<Predicate> {
            val queryLower = filters.query?.lowercase()
            if (!queryLower.isNullOrBlank()) {
                criteriaBuilder.or(
                    criteriaBuilder.like(
                        criteriaBuilder.lower(
                            root.get("name")
                        ),
                        "%$queryLower%",
                    ),
                    criteriaBuilder.like(
                        criteriaBuilder.lower(
                            root.get("description")
                        ),
                        "%$queryLower%",
                    ),
                ).let(::add)
            }

            filters.category?.let {
                add(
                    criteriaBuilder.equal(
                        root.get<EventCategory>("category"),
                        it
                    )
                )
            }

            filters.startDate?.let {
                add(
                    criteriaBuilder.greaterThanOrEqualTo(
                        root.get("date"),
                        it
                    )
                )
            }

            filters.endDate?.let {
                add(
                    criteriaBuilder.lessThanOrEqualTo(
                        root.get("date"),
                        it
                    )
                )
            }
        }
        return criteriaBuilder.and(*predicates.toTypedArray())
    }
}
package com.example.musala.services.events.dtos

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDate
import java.time.OffsetDateTime

@Entity
@Table(name = "events")
@EntityListeners(AuditingEntityListener::class)
class EventEntity(
    @Id
    @GeneratedValue(generator = "events_id_seq")
    @SequenceGenerator(name = "events_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    var id: Long? = null,
    var name: String = "",
    var date: LocalDate = LocalDate.now(),
    var availableAttendeesCount: Int = 1,
    var description: String = "",
    var category: EventCategory = EventCategory.Concert,
    /*@CreatedBy
    @JoinColumn(updatable = false)
    @ManyToOne(cascade = [CascadeType.REMOVE])
    var createdBy: UserEntity? = null,
    @LastModifiedBy
    @JoinColumn(insertable = false)
    @ManyToOne(cascade = [CascadeType.REMOVE])
    var lastModifiedBy: UserEntity? = null,*/
    @CreatedDate
    @Column(nullable = true, updatable = false)
    var dateCreated: OffsetDateTime? = null,
    @LastModifiedDate
    @Column(insertable = false)
    var dateLastModified: OffsetDateTime? = null,
)
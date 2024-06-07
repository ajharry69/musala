package com.example.musala.services.tickets.dtos

import com.example.musala.services.events.dtos.EventEntity
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime

@Entity
@Table(name = "tickets")
@EntityListeners(AuditingEntityListener::class)
class TicketEntity(
    @Id
    @GeneratedValue(generator = "tickets_id_seq")
    @SequenceGenerator(name = "tickets_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    var id: Long? = null,
    var attendeesCount: Int = 1,
    @ManyToOne(optional = false, cascade = [CascadeType.REMOVE])
    var event: EventEntity? = null,
    /*@CreatedBy
    @JoinColumn(updatable = false)
    @ManyToOne(cascade = [CascadeType.REMOVE])
    var createdBy: User? = null,
    @LastModifiedBy
    @JoinColumn(insertable = false)
    @ManyToOne(cascade = [CascadeType.REMOVE])
    var lastModifiedBy: User? = null,*/
    @CreatedDate
    @Column(nullable = true, updatable = false)
    var dateCreated: OffsetDateTime? = null,
    @LastModifiedDate
    @Column(insertable = false)
    var dateLastModified: OffsetDateTime? = null,
)
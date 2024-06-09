package com.example.musala.services.tickets.dtos

import com.example.musala.services.events.dtos.EventEntity
import com.example.musala.services.users.dtos.UserEntity
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
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
    var notified: Boolean = false,
    @CreatedBy
    @JoinColumn(updatable = false)
    @ManyToOne(cascade = [CascadeType.REMOVE])
    var reservedBy: UserEntity? = null,
    @CreatedDate
    @Column(nullable = true, updatable = false)
    var dateReserved: OffsetDateTime? = null,
)
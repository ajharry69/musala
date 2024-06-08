package com.example.musala.services.users.dtos

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
class UserEntity(
    @Id
    @Column(nullable = false)
    var id: UUID = UUID.randomUUID(),
    var name: String = "",
    var email: String = "",
    @Column(name = "password")
    var encodedPassword: String = "",
    @CreatedDate
    @Column(nullable = true, updatable = false)
    var dateCreated: OffsetDateTime? = null,
    @LastModifiedDate
    @Column(insertable = false)
    var dateLastModified: OffsetDateTime? = null,
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf()
    }

    override fun getPassword(): String {
        return encodedPassword
    }

    override fun getUsername(): String {
        return email
    }
}
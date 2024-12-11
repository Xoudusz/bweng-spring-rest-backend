package at.technikum.springrestbackend.entity

import at.technikum.springrestbackend.entity.enums.Role
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*


@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID = UUID.randomUUID(),

    @Column(unique = true)
    val username: String,

    @Column(unique = true)
    val email: String,

    val password: String,

    val role: Role,

    val salutation: String,

    val country: String,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
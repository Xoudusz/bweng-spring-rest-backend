package at.technikum.springrestbackend.entity

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

    val passwordHash: String,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
package at.technikum.springrestbackend.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
data class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID = UUID.randomUUID(),

    @JoinColumn(name = "user_Id", nullable = false)
    val userId: UUID,

    val content: String,

    val createdAt: LocalDateTime = LocalDateTime.now()
)

package at.technikum.springrestbackend.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID = UUID.randomUUID(),

    @JoinColumn(name = "author_id", nullable = false)
    val userId: UUID,

    @JoinColumn(name = "post_id", nullable = false)
    val postId: UUID,

    @Column(length = 500)
    val content: String,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)

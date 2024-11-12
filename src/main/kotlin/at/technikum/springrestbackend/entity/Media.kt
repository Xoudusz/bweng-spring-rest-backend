package at.technikum.springrestbackend.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID


@Entity
@Table(name = "media")
data class Media(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    val post: Post,

    @Column(nullable = false)
    val url: String,

    @Column(nullable = false)
    val type: String, // e.g., "image" or "video"

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)


package at.technikum.springrestbackend.entity

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "likes")
data class Like (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID = UUID.randomUUID(),

    @JoinColumn(name = "user_Id", nullable = false)
    val userId: UUID,

    @JoinColumn(name = "post_id", nullable = false)
    val postId: UUID,
    )


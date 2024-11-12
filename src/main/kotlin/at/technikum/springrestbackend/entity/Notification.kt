package at.technikum.springrestbackend.entity

import at.technikum.springrestbackend.entity.enums.NotificationType
import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
data class Notification(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID = UUID.randomUUID(),

    @JoinColumn(name = "user_id", nullable = false)
    val userId: UUID,

    @JoinColumn(name = "entity_id", nullable = false)
    val entityId: UUID? = null,

    val type: NotificationType,

    @Column(length = 65)
    var content: String,

    @Column(name = "is_read")
    var isRead: Boolean = false,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()

)

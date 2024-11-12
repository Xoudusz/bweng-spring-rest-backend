package at.technikum.springrestbackend.repository

import at.technikum.springrestbackend.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NotificationRepository : JpaRepository<Notification, UUID> {
    fun findByUserId(userId: UUID): List<Notification>
    fun findByUserIdAndEntityId(userId: UUID, entityId: UUID): List<Notification>
}
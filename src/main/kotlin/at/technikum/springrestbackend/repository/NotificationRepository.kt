package at.technikum.springrestbackend.repository

import at.technikum.springrestbackend.entity.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NotificationRepository: JpaRepository<Notification, UUID> {
    fun findByUserId(userId: UUID): List<Notification>
    fun findByUserIdAndPostId(userId: UUID, postId: UUID): List<Notification>
    fun findByUserIdAndCommentId(userId: UUID, commentId: UUID): List<Notification>
    fun findByUserIdAndLikeId(userId: UUID, likeId: UUID): List<Notification>
    fun findByUserIdAndFollowId(userId: UUID, followId: UUID): List<Notification>
}
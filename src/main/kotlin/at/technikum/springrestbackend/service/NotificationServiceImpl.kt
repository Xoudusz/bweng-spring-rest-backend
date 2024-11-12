package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.NotificationCreateDTO
import at.technikum.springrestbackend.dto.NotificationUpdateDTO
import at.technikum.springrestbackend.entity.Notification
import at.technikum.springrestbackend.entity.enums.NotificationType
import at.technikum.springrestbackend.exception.notFound.*
// import at.technikum.springrestbackend.exception.notFound.FollowNotFoundException
import at.technikum.springrestbackend.repository.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class NotificationServiceImpl @Autowired constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository,
    private val likeRepository: LikeRepository,
    private val notificationRepository: NotificationRepository,
    private val followRepository: FollowRepository
) : NotificationService {
    override fun getAllNotifications(): List<Notification> {
        return notificationRepository.findAll()
    }

    override fun createNotification(notificationCreateDTO: NotificationCreateDTO): Notification {
        if (!userRepository.existsById(notificationCreateDTO.userId)) {
            throw UserNotFoundException("User with ID ${notificationCreateDTO.userId} not found")
        }

        val notification = Notification(
            userId = notificationCreateDTO.userId,
            content = notificationCreateDTO.content,
            type = notificationCreateDTO.type,
        )

        val entityId = notificationCreateDTO.entityId
        val updatedNotification = when (notificationCreateDTO.type) {
            NotificationType.POST -> {
                if (!postRepository.existsById(entityId)) {
                    throw PostNotFoundException("Post with ID $entityId not found")
                }
                notification.copy(entityId = entityId)
            }

            NotificationType.COMMENT -> {
                if (!commentRepository.existsById(entityId)) {
                    throw CommentNotFoundException("Comment with ID $entityId not found")
                }
                notification.copy(entityId = entityId)
            }

            NotificationType.LIKE -> {
                if (!likeRepository.existsById(entityId)) {
                    throw LikeNotFoundException("Like with ID $entityId not found")
                }
                notification.copy(entityId = entityId)
            }

            NotificationType.FOLLOW -> {
                if (!followRepository.existsById(entityId)) {
                    throw FollowNotFoundException("Follow with ID $entityId not found")
                }
                notification.copy(entityId = entityId)
            }
        }

        return notificationRepository.save(updatedNotification)
    }

    override fun deleteNotification(notificationId: UUID) {
        if (!notificationRepository.existsById(notificationId)) {
            throw PostNotFoundException("Notification with ID $notificationId not found")
        }
        notificationRepository.deleteById(notificationId)
    }

    override fun deleteNotifications(userId: UUID) {
        if (!notificationRepository.existsById(userId)) {
            throw PostNotFoundException("User with ID $userId not found")
        }
        notificationRepository.deleteById(userId)
    }

    override fun getNotifications(userId: UUID): List<Notification> {
        if (!userRepository.existsById(userId)) {
            throw UserNotFoundException("User with ID $userId not found")
        }
        return notificationRepository.findByUserId(userId)
    }

    override fun getNotification(notificationId: UUID): Notification {
        if (!notificationRepository.existsById(notificationId)) {
            throw UserNotFoundException("User with ID $notificationId not found")
        }
        return notificationRepository.findById(notificationId).get()
    }

    override fun updateNotification(
        notificationId: UUID,
        notificationUpdateDTO: NotificationUpdateDTO
    ): Notification {
        val notificationExists = notificationRepository.findById(notificationId).orElseThrow {
            NotificationNotFoundException("Notification with ID $notificationId not found")
        }
        return notificationExists.copy(content = notificationUpdateDTO.content, isRead = notificationUpdateDTO.isRead).also { notificationRepository.save(it) }
    }

    override fun getUnreadNotifications(userId: UUID): List<Notification> {
        val userExists = userRepository.existsById(userId)
        if (!userExists) {
            throw UserNotFoundException("User with ID $userId not found")
        }
        return notificationRepository.findByUserId(userId).filter { !it.isRead }
    }

    override fun getUnreadNotificationCount(userId: UUID): Int {
        val userExists = userRepository.existsById(userId)
        if (!userExists) {
            throw UserNotFoundException("User with ID $userId not found")
        }
        return notificationRepository.findByUserId(userId).count { !it.isRead }
    }

    override fun getNotificationCount(userId: UUID): Int {
        val userExists = userRepository.existsById(userId)
        if (!userExists) {
            throw UserNotFoundException("User with ID $userId not found")
        }
        return notificationRepository.findByUserId(userId).size
    }

    override fun getNotificationCount(): Int {
        return notificationRepository.findAll().size
    }
}
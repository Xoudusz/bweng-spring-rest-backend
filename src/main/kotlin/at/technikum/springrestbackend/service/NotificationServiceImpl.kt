package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.NotificationCreateDTO
import at.technikum.springrestbackend.dto.NotificationUpdateDTO
import at.technikum.springrestbackend.entity.Notification
import at.technikum.springrestbackend.exception.*
// import at.technikum.springrestbackend.exception.FollowNotFoundException
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
    // private val followRepository: FollowRepository
) : NotificationService {
    override fun getAllNotifications(): List<Notification> {
        return notificationRepository.findAll()
    }

    override fun createNotification(notificationCreateDTO: NotificationCreateDTO): Notification {
        val userExists = userRepository.existsById(notificationCreateDTO.userId)
        if (!userExists) {
            throw UserNotFoundException("User with ID ${notificationCreateDTO.userId} not found")
        }

        when (notificationCreateDTO.type) {
            "POST" -> {
                val postExists = postRepository.existsById(notificationCreateDTO.postId)
                if (!postExists) {
                    throw PostNotFoundException("Post with ID ${notificationCreateDTO.postId} not found")
                }
            }

            "COMMENT" -> {
                val commentExists = commentRepository.existsById(notificationCreateDTO.commentId)
                if (!commentExists) {
                    throw CommentNotFoundException("Comment with ID ${notificationCreateDTO.commentId} not found")
                }
            }

            "LIKE" -> {
                val likeExists = likeRepository.existsById(notificationCreateDTO.likeId)
                if (!likeExists) {
                    throw LikeNotFoundException("Like with ID ${notificationCreateDTO.likeId} not found")
                }
            }/*
            "FOLLOW" -> {
                val followExists = followRepository.existsById(notificationCreateDTO.followId)
                if (!followExists) {
                    throw FollowNotFoundException("Follow with ID ${notificationCreateDTO.followId} not found")
                }
            }
            */
        }

        val notification = Notification(
            userId = notificationCreateDTO.userId,
            content = notificationCreateDTO.content,
            type = notificationCreateDTO.type,
        )

        return notificationRepository.save(notification)
    }

    override fun deleteNotification(notificationId: UUID) {
        if (!notificationRepository.existsById(notificationId)) {
            throw PostNotFoundException("Post with ID $notificationId not found")
        }
        notificationRepository.deleteById(notificationId)
    }

    override fun deleteNotifications(userId: UUID) {
        if (!notificationRepository.existsById(userId)) {
            throw PostNotFoundException("Post with ID $userId not found")
        }
        notificationRepository.deleteById(userId)
    }

    override fun getNotifications(userId: UUID): List<Notification> {
        val userExists = userRepository.existsById(userId)
        if (!userExists) {
            throw UserNotFoundException("User with ID $userId not found")
        }
        return notificationRepository.findByUserId(userId)
    }

    override fun getNotification(notificationId: UUID): Notification {
        val notificationExists = notificationRepository.existsById(notificationId)
        if (!notificationExists) {
            throw UserNotFoundException("User with ID $notificationId not found")
        }
        return notificationRepository.findById(notificationId).orElseThrow {
            UserNotFoundException("User with ID $notificationId not found")
        }
    }

    override fun updateNotification(notificationId: UUID, content: String): Notification {
        val notificationExists = notificationRepository.existsById(notificationId)
        if (!notificationExists) {
            throw UserNotFoundException("Notification with ID $notificationId not found")
        }
        val notification = notificationRepository.findById(notificationId).orElseThrow {
            NotificationNotFoundException("Notification with ID $notificationId not found")
        }
        notification.content = content
        return notificationRepository.save(notification)
    }

    override fun markNotificationAsRead(notificationId: UUID, notificationUpdateDTO: NotificationUpdateDTO): Notification {
        val notificationExists = notificationRepository.findById(notificationId).orElseThrow {
            NotificationNotFoundException("Notification with ID $notificationId not found")
        }
        return notificationExists.copy(content = notificationUpdateDTO.content).also { notificationRepository.save(it) }
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
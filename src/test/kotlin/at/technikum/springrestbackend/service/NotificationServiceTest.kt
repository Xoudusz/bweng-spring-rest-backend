package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.NotificationCreateDTO
import at.technikum.springrestbackend.dto.NotificationUpdateDTO
import at.technikum.springrestbackend.entity.Notification
import at.technikum.springrestbackend.entity.enums.NotificationType
import at.technikum.springrestbackend.exception.notFound.NotificationNotFoundException
import at.technikum.springrestbackend.exception.notFound.PostNotFoundException
import at.technikum.springrestbackend.exception.notFound.UserNotFoundException
import at.technikum.springrestbackend.repository.NotificationRepository
import at.technikum.springrestbackend.repository.PostRepository
import at.technikum.springrestbackend.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

class NotificationServiceTest {

    private val postRepository: PostRepository = mock(PostRepository::class.java)

    private val notificationRepository: NotificationRepository = mock(NotificationRepository::class.java)

    private val userRepository: UserRepository = mock(UserRepository::class.java)

    private val notificationService = NotificationServiceImpl(
        postRepository, // Other repositories
        userRepository,
        mock(),
        mock(),
        notificationRepository,
        mock()
    )

    @Test
    fun `should return all notifications`() {
        val notification1 = Notification(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            entityId = UUID.randomUUID(),
            type = NotificationType.POST,
            content = "Notification 1"
        )
        val notification2 = Notification(
            id = UUID.randomUUID(),
            userId = UUID.randomUUID(),
            entityId = UUID.randomUUID(),
            type = NotificationType.COMMENT,
            content = "Notification 2"
        )

        val notifications = listOf(notification1, notification2)

        `when`(notificationRepository.findAll()).thenReturn(notifications)

        val result = notificationService.getAllNotifications()

        assertEquals(2, result.size)
        assertEquals("Notification 1", result[0].content)
        assertEquals("Notification 2", result[1].content)
        verify(notificationRepository, times(1)).findAll()
    }

    @Test
    fun `should return empty list when no notifications exist`() {
        `when`(notificationRepository.findAll()).thenReturn(emptyList())

        val result = notificationService.getAllNotifications()

        assertTrue(result.isEmpty())
        verify(notificationRepository, times(1)).findAll()
    }


    @Test
    fun `should create notification successfully`() {
        val userId = UUID.randomUUID()
        val entityId = UUID.randomUUID()
        val dto = NotificationCreateDTO(userId, "Test notification", NotificationType.POST, entityId)

        // Mock user exists
        `when`(userRepository.existsById(userId)).thenReturn(true)
        // Mock post exists for NotificationType.POST
        `when`(postRepository.existsById(entityId)).thenReturn(true)
        // Mock saving the notification
        `when`(notificationRepository.save(any(Notification::class.java))).thenAnswer { it.arguments[0] }

        val result = notificationService.createNotification(dto)

        // Assert the created notification's details
        assertEquals("Test notification", result.content)
        assertEquals(NotificationType.POST, result.type)
        assertEquals(entityId, result.entityId)

        // Verify repository interactions
        verify(userRepository, times(1)).existsById(userId)
        verify(postRepository, times(1)).existsById(entityId)
        verify(notificationRepository, times(1)).save(any(Notification::class.java))
    }

    @Test
    fun `should delete notification successfully`() {
        val notificationId = UUID.randomUUID()

        `when`(notificationRepository.existsById(notificationId)).thenReturn(true)

        notificationService.deleteNotification(notificationId)

        verify(notificationRepository, times(1)).deleteById(notificationId)
        verify(notificationRepository, times(1)).existsById(notificationId)
    }

    @Test
    fun `should delete notifications for a user successfully`() {
        val userId = UUID.randomUUID()

        // Mock that the user's notifications exist
        `when`(notificationRepository.existsById(userId)).thenReturn(true)

        // Call the service method
        notificationService.deleteNotifications(userId)

        // Verify that the repository's deleteById method was called
        verify(notificationRepository, times(1)).deleteById(userId)
        verify(notificationRepository, times(1)).existsById(userId)
    }

    @Test
    fun `should retrieve notification successfully`() {
        val notificationId = UUID.randomUUID()
        val notification = Notification(
            id = notificationId,
            userId = UUID.randomUUID(),
            entityId = UUID.randomUUID(),
            type = NotificationType.POST,
            content = "Test notification"
        )

        // Mock that the notification exists
        `when`(notificationRepository.existsById(notificationId)).thenReturn(true)
        `when`(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification))

        // Call the service method
        val result = notificationService.getNotification(notificationId)

        // Assertions
        assertEquals(notification.id, result.id)
        assertEquals(notification.content, result.content)
        assertEquals(notification.type, result.type)

        // Verify repository interactions
        verify(notificationRepository, times(1)).existsById(notificationId)
        verify(notificationRepository, times(1)).findById(notificationId)
    }

    @Test
    fun `should throw UserNotFoundException when notification does not exist`() {
        val notificationId = UUID.randomUUID()

        // Mock that the notification does not exist
        `when`(notificationRepository.existsById(notificationId)).thenReturn(false)

        // Assert that the exception is thrown
        val exception = assertThrows<UserNotFoundException> {
            notificationService.getNotification(notificationId)
        }

        // Assert the exception message
        assertEquals("User with ID $notificationId not found", exception.message)

        // Verify repository interactions
        verify(notificationRepository, times(1)).existsById(notificationId)
        verify(notificationRepository, never()).findById(notificationId)
    }


    @Test
    fun `should throw PostNotFoundException when user does not exist`() {
        val userId = UUID.randomUUID()

        `when`(notificationRepository.existsById(userId)).thenReturn(false)

        val exception = assertThrows<PostNotFoundException> {
            notificationService.deleteNotifications(userId)
        }

        assertEquals("User with ID $userId not found", exception.message)

        verify(notificationRepository, never()).deleteById(userId)
        verify(notificationRepository, times(1)).existsById(userId)
    }


    @Test
    fun `should throw PostNotFoundException when notification does not exist`() {
        val notificationId = UUID.randomUUID()

        `when`(notificationRepository.existsById(notificationId)).thenReturn(false)

        val exception = assertThrows<PostNotFoundException> {
            notificationService.deleteNotification(notificationId)
        }

        assertEquals("Notification with ID $notificationId not found", exception.message)

        verify(notificationRepository, never()).deleteById(notificationId)
        verify(notificationRepository, times(1)).existsById(notificationId)
    }



    @Test
    fun `should throw UserNotFoundException when creating notification with invalid user`() {
        val userId = UUID.randomUUID()
        val dto = NotificationCreateDTO(userId, "Test notification", NotificationType.POST, UUID.randomUUID())

        `when`(userRepository.existsById(userId)).thenReturn(false)

        assertThrows<UserNotFoundException> {
            notificationService.createNotification(dto)
        }
    }



    @Test
    fun `should retrieve notifications for a user`() {
        val userId = UUID.randomUUID()
        val notifications = listOf(
            Notification(UUID.randomUUID(), userId, UUID.randomUUID(), NotificationType.POST, "Test notification")
        )

        `when`(userRepository.existsById(userId)).thenReturn(true)
        `when`(notificationRepository.findByUserId(userId)).thenReturn(notifications)

        val result = notificationService.getNotifications(userId)

        assertEquals(1, result.size)
        assertEquals("Test notification", result[0].content)
        verify(notificationRepository, times(1)).findByUserId(userId)
    }

    @Test
    fun `should throw UserNotFoundException when retrieving notifications for invalid user`() {
        val userId = UUID.randomUUID()

        `when`(userRepository.existsById(userId)).thenReturn(false)

        assertThrows<UserNotFoundException> {
            notificationService.getNotifications(userId)
        }
    }
    @Test
    fun `should update notification successfully`() {
        val notificationId = UUID.randomUUID()
        val notificationUpdateDTO = NotificationUpdateDTO(content = "Updated content", isRead = true)
        val existingNotification = Notification(
            id = notificationId,
            userId = UUID.randomUUID(),
            entityId = UUID.randomUUID(),
            type = NotificationType.POST,
            content = "Old content",
            isRead = false
        )

        `when`(notificationRepository.findById(notificationId)).thenReturn(Optional.of(existingNotification))
        `when`(notificationRepository.save(any(Notification::class.java))).thenAnswer { it.arguments[0] }

        val result = notificationService.updateNotification(notificationId, notificationUpdateDTO)

        assertEquals("Updated content", result.content)
        assertTrue(result.isRead)
        verify(notificationRepository, times(1)).save(result)
        verify(notificationRepository, times(1)).findById(notificationId)
    }

    @Test
    fun `should throw NotificationNotFoundException when notification does not exist`() {
        val notificationId = UUID.randomUUID()
        val notificationUpdateDTO = NotificationUpdateDTO(content = "Updated content", isRead = true)

        `when`(notificationRepository.findById(notificationId)).thenReturn(Optional.empty())

        val exception = assertThrows<NotificationNotFoundException> {
            notificationService.updateNotification(notificationId, notificationUpdateDTO)
        }

        assertEquals("Notification with ID $notificationId not found", exception.message)
        verify(notificationRepository, times(1)).findById(notificationId)
        verify(notificationRepository, never()).save(any())
    }
    @Test
    fun `should return unread notifications for a user`() {
        val userId = UUID.randomUUID()
        val notifications = listOf(
            Notification(UUID.randomUUID(), userId, UUID.randomUUID(), NotificationType.POST, "Unread notification 1", isRead = false),
            Notification(UUID.randomUUID(), userId, UUID.randomUUID(), NotificationType.POST, "Read notification", isRead = true),
            Notification(UUID.randomUUID(), userId, UUID.randomUUID(), NotificationType.POST, "Unread notification 2", isRead = false)
        )

        `when`(userRepository.existsById(userId)).thenReturn(true)
        `when`(notificationRepository.findByUserId(userId)).thenReturn(notifications)

        val result = notificationService.getUnreadNotifications(userId)

        assertEquals(2, result.size)
        assertTrue(result.all { !it.isRead })
        verify(userRepository, times(1)).existsById(userId)
        verify(notificationRepository, times(1)).findByUserId(userId)
    }

    @Test
    fun `should throw UserNotFoundException when retrieving unread notifications for invalid user`() {
        val userId = UUID.randomUUID()

        `when`(userRepository.existsById(userId)).thenReturn(false)

        val exception = assertThrows<UserNotFoundException> {
            notificationService.getUnreadNotifications(userId)
        }

        assertEquals("User with ID $userId not found", exception.message)
        verify(userRepository, times(1)).existsById(userId)
        verify(notificationRepository, never()).findByUserId(userId)
    }

    @Test
    fun `should return unread notification count for a user`() {
        val userId = UUID.randomUUID()
        val notifications = listOf(
            Notification(UUID.randomUUID(), userId, UUID.randomUUID(), NotificationType.POST, "Unread notification 1", isRead = false),
            Notification(UUID.randomUUID(), userId, UUID.randomUUID(), NotificationType.POST, "Read notification", isRead = true),
            Notification(UUID.randomUUID(), userId, UUID.randomUUID(), NotificationType.POST, "Unread notification 2", isRead = false)
        )

        `when`(userRepository.existsById(userId)).thenReturn(true)
        `when`(notificationRepository.findByUserId(userId)).thenReturn(notifications)

        val result = notificationService.getUnreadNotificationCount(userId)

        assertEquals(2, result)
        verify(userRepository, times(1)).existsById(userId)
        verify(notificationRepository, times(1)).findByUserId(userId)
    }

    @Test
    fun `should throw UserNotFoundException when retrieving unread notification count for invalid user`() {
        val userId = UUID.randomUUID()

        `when`(userRepository.existsById(userId)).thenReturn(false)

        val exception = assertThrows<UserNotFoundException> {
            notificationService.getUnreadNotificationCount(userId)
        }

        assertEquals("User with ID $userId not found", exception.message)
        verify(userRepository, times(1)).existsById(userId)
        verify(notificationRepository, never()).findByUserId(userId)
    }

    @Test
    fun `should return notification count for a user`() {
        val userId = UUID.randomUUID()
        val notifications = listOf(
            Notification(UUID.randomUUID(), userId, UUID.randomUUID(), NotificationType.POST, "Notification 1"),
            Notification(UUID.randomUUID(), userId, UUID.randomUUID(), NotificationType.POST, "Notification 2")
        )

        `when`(userRepository.existsById(userId)).thenReturn(true)
        `when`(notificationRepository.findByUserId(userId)).thenReturn(notifications)

        val result = notificationService.getNotificationCount(userId)

        assertEquals(2, result)
        verify(userRepository, times(1)).existsById(userId)
        verify(notificationRepository, times(1)).findByUserId(userId)
    }

    @Test
    fun `should throw UserNotFoundException when retrieving notification count for invalid user`() {
        val userId = UUID.randomUUID()

        `when`(userRepository.existsById(userId)).thenReturn(false)

        val exception = assertThrows<UserNotFoundException> {
            notificationService.getNotificationCount(userId)
        }

        assertEquals("User with ID $userId not found", exception.message)
        verify(userRepository, times(1)).existsById(userId)
        verify(notificationRepository, never()).findByUserId(userId)
    }

    @Test
    fun `should return total notification count`() {
        val notifications = listOf(
            Notification(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), NotificationType.POST, "Notification 1"),
            Notification(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), NotificationType.POST, "Notification 2")
        )

        `when`(notificationRepository.findAll()).thenReturn(notifications)

        val result = notificationService.getNotificationCount()

        assertEquals(2, result)
        verify(notificationRepository, times(1)).findAll()
    }




}

package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.NotificationCreateDTO
import at.technikum.springrestbackend.dto.NotificationUpdateDTO
import at.technikum.springrestbackend.entity.Notification
import at.technikum.springrestbackend.service.NotificationServiceImpl
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController(private val notificationServiceImpl: NotificationServiceImpl) {

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}")
    fun getNotifications(@PathVariable userId: UUID): ResponseEntity<List<Notification>> {
        val notifications = notificationServiceImpl.getNotifications(userId)
        return ResponseEntity(notifications, HttpStatus.OK)
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}/unread")
    fun getUnreadNotifications(@PathVariable userId: UUID): ResponseEntity<List<Notification>> {
        val notifications = notificationServiceImpl.getUnreadNotifications(userId)
        return ResponseEntity(notifications, HttpStatus.OK)
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}/unread/count")
    fun getUnreadNotificationCount(@PathVariable userId: UUID): ResponseEntity<Int> {
        val notifications = notificationServiceImpl.getNotificationCount(userId)
        return ResponseEntity(notifications, HttpStatus.OK)
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{userId}/count")
    fun getNotificationCount(@PathVariable userId: UUID): ResponseEntity<Int> {
        val notifications = notificationServiceImpl.getNotificationCount(userId)
        return ResponseEntity(notifications, HttpStatus.OK)
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{notificationId}")
    fun getNotification(@PathVariable notificationId: UUID): ResponseEntity<Notification> {
        val notifications = notificationServiceImpl.getNotification(notificationId)
        return ResponseEntity(notifications, HttpStatus.OK)
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    fun createNotification(@RequestBody notificationCreateDTO: NotificationCreateDTO): ResponseEntity<Notification> {
        return ResponseEntity(notificationServiceImpl.createNotification(notificationCreateDTO), HttpStatus.CREATED)
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{notificationId}")
    fun updateNotification(
        @PathVariable notificationId: UUID,
        @RequestBody @Valid notificationUpdateDTO: NotificationUpdateDTO
    ): ResponseEntity<Notification> {
        val readNotification = notificationServiceImpl.updateNotification(notificationId, notificationUpdateDTO)
        return ResponseEntity(readNotification, HttpStatus.OK)
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{notificationId}")
    fun deleteNotification(@PathVariable notificationId: UUID): ResponseEntity<Void> {
        notificationServiceImpl.deleteNotification(notificationId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/user/{userId}")
    fun deleteNotifications(@PathVariable userId: UUID): ResponseEntity<Void> {
        notificationServiceImpl.deleteNotifications(userId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
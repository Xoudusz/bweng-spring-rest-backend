package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.NotificationCreateDTO
import at.technikum.springrestbackend.dto.NotificationUpdateDTO
import at.technikum.springrestbackend.entity.Notification
import org.springframework.stereotype.Service
import java.util.*

@Service
interface NotificationService {
    fun getAllNotifications(): List<Notification>
    fun createNotification(notificationCreateDTO: NotificationCreateDTO): Notification
    fun deleteNotification(notificationId: UUID)
    fun deleteNotifications(userId: UUID)
    fun getNotifications(userId: UUID): List<Notification>
    fun getNotification(notificationId: UUID): Notification
    fun updateNotification(notificationId: UUID, notificationUpdateDTO: NotificationUpdateDTO): Notification
    fun getUnreadNotifications(userId: UUID): List<Notification>
    fun getUnreadNotificationCount(userId: UUID): Int
    fun getNotificationCount(userId: UUID): Int
    fun getNotificationCount(): Int
}
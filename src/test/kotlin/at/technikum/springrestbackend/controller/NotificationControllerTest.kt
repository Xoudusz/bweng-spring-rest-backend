package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.NotificationCreateDTO
import at.technikum.springrestbackend.entity.Notification
import at.technikum.springrestbackend.entity.enums.NotificationType
import at.technikum.springrestbackend.service.NotificationServiceImpl
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

class NotificationControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var notificationService: NotificationServiceImpl

    @InjectMocks
    private lateinit var notificationController: NotificationController


    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build()
    }

    @Test
    fun `should retrieve notifications`() {
        val userId = UUID.randomUUID()
        val notifications = listOf(
            Notification(UUID.randomUUID(), userId, UUID.randomUUID(), NotificationType.POST, "Test notification")
        )

        `when`(notificationService.getNotifications(userId)).thenReturn(notifications)

        mockMvc.perform(get("/api/notifications/$userId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].content").value("Test notification"))

        verify(notificationService, times(1)).getNotifications(userId)
    }

    @Test
    fun `should create notification successfully`() {
        val userId = UUID.randomUUID()
        val dto = NotificationCreateDTO(userId, "Test notification", NotificationType.POST, UUID.randomUUID())
        val notification = Notification(UUID.randomUUID(), userId, UUID.randomUUID(), NotificationType.POST, "Test notification")

        `when`(notificationService.createNotification(dto)).thenReturn(notification)

        mockMvc.perform(
            post("/api/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.content").value("Test notification"))

        verify(notificationService, times(1)).createNotification(dto)
    }
}

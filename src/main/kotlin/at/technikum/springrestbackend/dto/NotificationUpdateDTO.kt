package at.technikum.springrestbackend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class NotificationUpdateDTO(
    @field:NotBlank(message = "Content is required")
    @field:Size(max = 65, message = "Content must not exceed 65 characters")
    val content: String,

    val isRead: Boolean = false
)
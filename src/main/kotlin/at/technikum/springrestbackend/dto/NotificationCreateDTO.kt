package at.technikum.springrestbackend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull
import java.util.*

data class NotificationCreateDTO(
    @field:NotNull
    val userId: UUID,

    @field:NotBlank(message = "Content is required")
    @field:Size(max = 65, message = "Content must not exceed 65 characters")
    val content: String,

    @field:NotBlank(message = "Type is required")
    val type: String,

    val postId: UUID,

    val commentId: UUID,

    val likeId: UUID,

    val followId: UUID

)
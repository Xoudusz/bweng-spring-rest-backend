package at.technikum.springrestbackend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.jetbrains.annotations.NotNull
import java.util.*

data class PostCreateDTO(
    @field:NotBlank(message = "Content is required")
    @field:Size(max = 500, message = "Content must not exceed 500 characters")
    val content: String,

    @field:NotNull
    val userId: UUID

)

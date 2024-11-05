package at.technikum.springrestbackend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.*

data class PostCreateDTO(
    @NotBlank(message = "Content is required")
    @Size(max = 280, message = "Content must not exceed 280 characters")
    val content: String,

    @NotBlank(message = "Author is required")
    val userId: UUID

)

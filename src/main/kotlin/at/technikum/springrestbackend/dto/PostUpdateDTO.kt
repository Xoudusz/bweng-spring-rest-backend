package at.technikum.springrestbackend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PostUpdateDTO(
    @NotBlank(message = "Content is required")
    @Size(max = 280, message = "Content must not exceed 280 characters")
    val content: String
    // No authorId here
)
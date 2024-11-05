package at.technikum.springrestbackend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class PostUpdateDTO(
    @field:NotBlank(message = "Content is required")
    @field:Size(max = 500, message = "Content must not exceed 500 characters")
    val content: String
)
package at.technikum.springrestbackend.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.util.*

data class MediaDTO(
    @field:NotNull(message = "Id cannot be null")
    val postId: UUID,
    @field:Pattern(
        regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$",
        message = "Invalid URL format")
    val url: String,
    @field:NotNull(message = "Type cannot be null")
    val type: String
)

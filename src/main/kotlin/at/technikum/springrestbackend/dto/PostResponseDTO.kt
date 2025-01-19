package at.technikum.springrestbackend.dto

import at.technikum.springrestbackend.entity.File
import java.time.LocalDateTime
import java.util.*

data class PostResponseDTO(
    val id: UUID,
    val content: String,
    val username: String,
    val createdAt: LocalDateTime,
    val file: File?,
    val profilePicture: File?

)
package at.technikum.springrestbackend.dto

import org.jetbrains.annotations.NotNull
import java.util.*

data class LikeCreateDTO(
    @field:NotNull
    val userId: UUID,

    @field:NotNull
    val postId: UUID

)

package at.technikum.springrestbackend.dto


import jakarta.validation.constraints.NotNull
import java.util.*

data class FollowDTO(
    @field:NotNull(message = "Follower Id cannot be null")
    val followerId: UUID,

    @field:NotNull(message = "Following Id cannot be null")
    val followingId: UUID
)

package at.technikum.springrestbackend.dto

import jakarta.validation.constraints.NotNull


data class ProfileVisibilityDTO(
    @field:NotNull(message = "Visibility status cannot be null")
    val isPrivate: Boolean
)
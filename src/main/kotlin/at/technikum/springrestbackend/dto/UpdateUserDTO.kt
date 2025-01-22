package at.technikum.springrestbackend.dto

import at.technikum.springrestbackend.dto.validation.ValidCountryCode
import at.technikum.springrestbackend.dto.validation.ValidRole
import at.technikum.springrestbackend.entity.enums.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UpdateUserDTO(
    @field:Size(min = 5, max = 50, message = "Username must be between 5 and 50 characters")
    @field:Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    val username: String? = null,

    @field:Email(message = "Email should be valid")
    @field:Size(max = 100, message = "Email should not exceed 100 characters")
    val email: String? = null,

    @field:Size(min = 8, message = "Password must be at least 8 characters")
    @field:Pattern(
        regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    val password: String? = null,

    @field:ValidRole(message = "Invalid role. Accepted values are USER and ADMIN")
    val role: Role? = null,

    val salutation: String? = null,

    @field:ValidCountryCode(message = "Country code must be a valid ISO country code.")
    val country: String? = null,

    val fileId: String? = null,

    val locked: Boolean? = false
)

package at.technikum.springrestbackend.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class UserDTO(
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    val username: String,

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    @Size(max = 100, message = "Email should not exceed 100 characters")
    val email: String,

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    val passwordHash: String
)

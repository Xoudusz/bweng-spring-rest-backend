package at.technikum.springrestbackend.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    @Email
    val email: String,

    @Column(nullable = false, unique = true)
    @Size(min = 5)
    val username: String,

    @Column(nullable = false)
    @Size(min = 8)
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+\$",
        message = "Password must contain at least one number, one lowercase, and one uppercase character"
    )
    val password: String,

    @Column(nullable = false)
    val country: String,

    val profilePicture: String? = null,  // Can be a URL or a path to the storage

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val role: UserRole = UserRole.USER,  // Enum for USER and ADMIN roles

    @CreationTimestamp
    val createdAt: LocalDateTime? = null,

    @UpdateTimestamp
    val updatedAt: LocalDateTime? = null
)

enum class UserRole {
    USER, ADMIN
}

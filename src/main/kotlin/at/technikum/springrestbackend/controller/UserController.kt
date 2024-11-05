package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.UserDTO
import at.technikum.springrestbackend.entity.User
import at.technikum.springrestbackend.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID


@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun createUser(@Valid @RequestBody userDTO: UserDTO): ResponseEntity<User> {
        val user = User(
            username = userDTO.username,
            email = userDTO.email,
            passwordHash = userDTO.passwordHash
        )
        val createdUser = userService.createUser(user)
        return ResponseEntity(createdUser, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<User> {
        val user = userService.getUserById(id)
        return if (user != null) {
            ResponseEntity(user, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<User>> {
        val users = userService.getAllUsers()
        return ResponseEntity(users, HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @Valid @RequestBody userDTO: UserDTO): ResponseEntity<User> {
        val user = User(
            username = userDTO.username,
            email = userDTO.email,
            passwordHash = userDTO.passwordHash
        )
        val updatedUser = userService.updateUser(id, user)
        return if (updatedUser != null) {
            ResponseEntity(updatedUser, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (userService.deleteUser(id)) {
            ResponseEntity(HttpStatus.NO_CONTENT)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<User> {
        val user = userService.findByEmail(email)
        return if (user != null) {
            ResponseEntity(user, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping("/username/{username}")
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<User> {
        val user = userService.findByUsername(username)
        return if (user != null) {
            ResponseEntity(user, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}

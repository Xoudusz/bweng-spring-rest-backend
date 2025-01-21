package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.UpdateUserDTO
import at.technikum.springrestbackend.dto.UserDTO
import at.technikum.springrestbackend.entity.User
import at.technikum.springrestbackend.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun createUser(@Valid @RequestBody userDTO: UserDTO): ResponseEntity<User> {
        val createdUser = userService.createUser(userDTO)
        return ResponseEntity(createdUser, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): ResponseEntity<User> {
        val user = userService.getUserById(id)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<User>> {
        val users = userService.getAllUsers()
        return ResponseEntity(users, HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @Valid @RequestBody userDTO: UpdateUserDTO): ResponseEntity<User> {
        val updatedUser = userService.updateUser(id, userDTO)
        return ResponseEntity(updatedUser, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID): ResponseEntity<Void> {
        userService.deleteUser(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<User> {
        val user = userService.findByEmail(email)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @GetMapping("/username/{username}")
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<User> {
        val user = userService.findByUsername(username)
        return ResponseEntity(user, HttpStatus.OK)
    }
}

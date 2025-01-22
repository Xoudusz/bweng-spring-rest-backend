package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.controller.UserController
import at.technikum.springrestbackend.dto.UpdateUserDTO
import at.technikum.springrestbackend.dto.UserDTO
import at.technikum.springrestbackend.entity.User
import at.technikum.springrestbackend.entity.enums.Role
import at.technikum.springrestbackend.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import java.util.*
import kotlin.test.assertEquals

class UserControllerTest {

    @Mock
    private lateinit var userService: UserService

    @InjectMocks
    private lateinit var userController: UserController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    //createUserTests
    @Test
    fun `should create user and return CREATED status`() {
        // Arrange
        val userDTO = UserDTO("John", "Doe", "johndoe@example.com", Role.USER, "Mr", "AUT")
        val createdUser = User(UUID.randomUUID(), "John", "Doe", "johndoe@example.com", Role.USER, "Mr", "AUT")
        `when`(userService.createUser(userDTO)).thenReturn(createdUser)

        // Act
        val response = userController.createUser(userDTO)

        // Assert
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(createdUser, response.body)
        verify(userService, times(1)).createUser(userDTO)
    }

    //getUserByIdTests
    @Test
    fun `should retrieve user by ID and return OK status`() {
        // Arrange
        val userId = UUID.randomUUID()
        val user = User(UUID.randomUUID(), "John", "Doe", "johndoe@example.com", Role.USER, "Mr", "AUT")
        `when`(userService.getUserById(userId)).thenReturn(user)

        // Act
        val response = userController.getUserById(userId)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(user, response.body)
        verify(userService, times(1)).getUserById(userId)
    }

    //getAllUsersTests
    @Test
    fun `should retrieve all users and return OK status`() {
        // Arrange
        val users = listOf(
            User(UUID.randomUUID(), "John", "Doe", "johndoe@example.com", Role.USER, "Mr", "AUT"),
            User(UUID.randomUUID(), "Jane", "Smith", "janesmith@example.com", Role.USER, "Mr", "AUT")
        )
        `when`(userService.getAllUsers()).thenReturn(users)

        // Act
        val response = userController.getAllUsers()

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(users, response.body)
        verify(userService, times(1)).getAllUsers()
    }

    //updateUserTests
    @Test
    fun `should update user and return OK status`() {
        // Arrange
        val userId = UUID.randomUUID()
        val userDTO = UpdateUserDTO("John", "Doe", "johndoe@example.com", Role.USER, "Mr", "AUT")
        val updatedUser = User(userId, "John", "Doe", "johndoe@example.com", Role.USER, "Mr", "AUT")
        `when`(userService.updateUser(userId, userDTO)).thenReturn(updatedUser)

        // Act
        val response = userController.updateUser(userId, userDTO)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updatedUser, response.body)
        verify(userService, times(1)).updateUser(userId, userDTO)
    }

    //deleteUserTests
    @Test
    fun `should delete user and return NO_CONTENT status`() {
        // Arrange
        val userId = UUID.randomUUID()

        // Act
        val response = userController.deleteUser(userId)

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify(userService, times(1)).deleteUser(userId)
    }

    //getUserByEmailTests
    @Test
    fun `should retrieve user by email and return OK status`() {
        // Arrange
        val email = "johndoe@example.com"
        val user = User(UUID.randomUUID(), "johndoe@example.com", email, "johndoe", Role.USER, "Mr", "AUT")
        `when`(userService.findByEmail(email)).thenReturn(user)

        // Act
        val response = userController.getUserByEmail(email)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(user, response.body)
        verify(userService, times(1)).findByEmail(email)
    }

    //getUserByUsernameTests
    @Test
    fun `should retrieve user by username and return OK status`() {
        // Arrange
        val username = "johndoe"
        val user = User(UUID.randomUUID(), username, "johndoe@example.com", "johndoe", Role.USER, "Mr", "AUT")
        `when`(userService.findByUsername(username)).thenReturn(user)

        // Act
        val response = userController.getUserByUsername(username)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(user, response.body)
        verify(userService, times(1)).findByUsername(username)
    }



}

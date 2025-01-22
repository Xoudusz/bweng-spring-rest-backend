package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.UpdateUserDTO
import at.technikum.springrestbackend.dto.UserDTO
import at.technikum.springrestbackend.entity.User
import at.technikum.springrestbackend.entity.enums.Role
import at.technikum.springrestbackend.exception.notFound.UserNotFoundException
import at.technikum.springrestbackend.repository.FileRepository
import at.technikum.springrestbackend.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class UserServiceTest {

    private lateinit var userService: UserServiceImpl
    private lateinit var userRepository: UserRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var fileRepository: FileRepository

    @BeforeEach
    fun setUp() {
        userRepository = mock()
        passwordEncoder = mock()
        fileRepository = mock()
        userService = UserServiceImpl(userRepository, passwordEncoder, fileRepository)
    }

    @Test
    fun `createUser should save a new user when username and email are unique`() {
        // Arrange
        val userDTO = UserDTO(
            username = "john_doe",
            email = "john.doe@example.com",
            password = "Strong@123",
            role = Role.USER,
            salutation = "Mr.",
            country = "US"
        )
        val encodedPassword = "encoded_password"
        val user = User(
            username = userDTO.username,
            email = userDTO.email,
            password = encodedPassword,
            role = userDTO.role,
            country = userDTO.country,
            salutation = userDTO.salutation
        )

        val savedUser = user.copy(id = UUID.randomUUID()) // Mock an id being set after saving
        whenever(userRepository.findByUsername(userDTO.username)).thenReturn(null)
        whenever(userRepository.findByEmail(userDTO.email)).thenReturn(null)
        whenever(passwordEncoder.encode(userDTO.password)).thenReturn(encodedPassword)
        whenever(userRepository.save(any())).thenReturn(savedUser) // Correct stubbing here

        // Act
        val result = userService.createUser(userDTO)

        // Assert
        assertNotNull(result)
        assertEquals(user.username, result.username)
        assertEquals(user.email, result.email)
        verify(userRepository).findByUsername(userDTO.username)
        verify(userRepository).findByEmail(userDTO.email)
        verify(passwordEncoder).encode(userDTO.password)
        verify(userRepository).save(any())
    }

    @Test
    fun `createUser should throw IllegalArgumentException when username already exists`() {
        val userDTO = UserDTO(
            username = "john_doe",
            email = "john.doe@example.com",
            password = "Strong@123",
            role = Role.USER,
            salutation = "Mr.",
            country = "US"
        )

        val existingUser = User(
            username = userDTO.username,
            email = "some.other@example.com",
            password = "encoded_password",
            role = userDTO.role,
            country = userDTO.country,
            salutation = userDTO.salutation
        )
        whenever(userRepository.findByUsername(userDTO.username)).thenReturn(existingUser)

        val exception = assertThrows<IllegalArgumentException> {
            userService.createUser(userDTO)
        }
        assertEquals("Username '${userDTO.username}' is already in use.", exception.message)
        verify(userRepository).findByUsername(userDTO.username)
        verify(userRepository, never()).findByEmail(any())
        verify(userRepository, never()).save(any())

    }

    @Test
    fun `createUser should throw IllegalArgumentException when email already exists`() {
        val userDTO = UserDTO(
            username = "john_doe",
            email = "john.doe@example.com",
            password = "Strong@123",
            role = Role.USER,
            salutation = "Mr.",
            country = "US"
        )
        val existingUser = User(
            username = "some_user",
            email = userDTO.email,
            password = "encoded_password",
            role = userDTO.role,
            country = userDTO.country,
            salutation = userDTO.salutation
        )

        whenever(userRepository.findByUsername(userDTO.username)).thenReturn(null)
        whenever(userRepository.findByEmail(userDTO.email)).thenReturn(existingUser)

        val exception = assertThrows<IllegalArgumentException> {
            userService.createUser(userDTO)
        }

        assertEquals("Email '${userDTO.email}' is already in use.", exception.message)
        verify(userRepository).findByUsername(userDTO.username)
        verify(userRepository).findByEmail(userDTO.email)
        verify(userRepository, never()).save(any())
    }


    @Test
    fun `getUserById should return a user when ID exists`() {
        val id = UUID.randomUUID()
        val user = User(
            id = id,
            username = "john_doe",
            email = "john.doe@example.com",
            password = "encoded_password",
            role = Role.USER,
            country = "US",
            salutation = "Mr."
        )
        whenever(userRepository.existsById(id)).thenReturn(true)
        whenever(userRepository.findById(id)).thenReturn(Optional.of(user))

        val result = userService.getUserById(id)

        assertNotNull(result)
        assertEquals(user.id, result.id)
        assertEquals(user.username, result.username)
        verify(userRepository).existsById(id)
        verify(userRepository).findById(id)
    }

    @Test
    fun `getUserById should throw UserNotFoundException when ID does not exist`() {
        val id = UUID.randomUUID()
        whenever(userRepository.existsById(id)).thenReturn(false)

        val exception = assertThrows<UserNotFoundException> {
            userService.getUserById(id)
        }
        assertEquals("User with ID $id not found", exception.message)
        verify(userRepository).existsById(id)
        verify(userRepository, never()).findById(any())
    }

    @Test
    fun `getAllUsers should return a list of users`() {
        val users = listOf(
            User(
                username = "john_doe",
                email = "john.doe@example.com",
                password = "encoded_password",
                role = Role.USER,
                country = "US",
                salutation = "Mr."
            )
        )
        whenever(userRepository.findAll()).thenReturn(users)

        val result = userService.getAllUsers()

        assertEquals(users.size, result.size)
        verify(userRepository).findAll()
    }

    @Test
    fun `updateUser should update and return an existing user`() {
        val id = UUID.randomUUID()
        val updateUserDTO = UpdateUserDTO(
            username = "john_doe_updated",
            email = "john.doe.updated@example.com",
            password = "Updated@123",
            role = Role.ADMIN,
            salutation = "Dr.",
            country = "GB"
        )
        val existingUser = User(
            id = id,
            username = "john_doe",
            email = "john.doe@example.com",
            password = "encoded_password",
            role = Role.USER,
            country = "US",
            salutation = "Mr."
        )
        val updatedUser = existingUser.copy(
            username = updateUserDTO.username!!,
            email = updateUserDTO.email!!,
            password = "updated_encoded_password",
            role = updateUserDTO.role!!,
            country = updateUserDTO.country!!,
            salutation = updateUserDTO.salutation!!
        )
        val savedUpdatedUser = updatedUser.copy(id = id)


        whenever(userRepository.findById(id)).thenReturn(Optional.of(existingUser))
        whenever(passwordEncoder.encode(updateUserDTO.password)).thenReturn("updated_encoded_password")
        whenever(userRepository.save(any())).thenReturn(savedUpdatedUser)

        val result = userService.updateUser(id, updateUserDTO)

        assertEquals(updateUserDTO.username, result.username)
        assertEquals(updateUserDTO.email, result.email)
        assertEquals(updateUserDTO.role, result.role)
        assertEquals(updateUserDTO.salutation, result.salutation)
        assertEquals(updateUserDTO.country, result.country)


        verify(userRepository).findById(id)
        verify(passwordEncoder).encode(updateUserDTO.password)
        verify(userRepository).save(any())
    }

    @Test
    fun `updateUser should throw UserNotFoundException when user does not exist`() {
        val id = UUID.randomUUID()
        val updateUserDTO = UpdateUserDTO(
            username = "john_doe_updated",
            email = "john.doe.updated@example.com",
            password = "Updated@123",
            role = Role.ADMIN,
            salutation = "Dr.",
            country = "GB"
        )

        whenever(userRepository.findById(id)).thenReturn(Optional.empty())

        val exception = assertThrows<UserNotFoundException> {
            userService.updateUser(id, updateUserDTO)
        }
        assertEquals("User with ID $id not found", exception.message)

        verify(userRepository).findById(id)
        verify(userRepository, never()).save(any())
    }


    @Test
    fun `deleteUser should delete a user when ID exists`() {
        val id = UUID.randomUUID()
        whenever(userRepository.existsById(id)).thenReturn(true)
        doNothing().whenever(userRepository).deleteById(id)

        userService.deleteUser(id)

        verify(userRepository).existsById(id)
        verify(userRepository).deleteById(id)
    }

    @Test
    fun `deleteUser should throw UserNotFoundException when ID does not exist`() {
        val id = UUID.randomUUID()
        whenever(userRepository.existsById(id)).thenReturn(false)

        val exception = assertThrows<UserNotFoundException> {
            userService.deleteUser(id)
        }
        assertEquals("User with ID $id not found", exception.message)

        verify(userRepository).existsById(id)
        verify(userRepository, never()).deleteById(any())
    }

    @Test
    fun `findByEmail should return a user when email exists`() {
        val email = "john.doe@example.com"
        val user = User(
            username = "john_doe",
            email = email,
            password = "encoded_password",
            role = Role.USER,
            country = "US",
            salutation = "Mr."
        )
        whenever(userRepository.findByEmail(email)).thenReturn(user)

        val result = userService.findByEmail(email)

        assertNotNull(result)
        assertEquals(user.email, result.email)
        verify(userRepository).findByEmail(email)
    }

    @Test
    fun `findByEmail should throw UserNotFoundException when email does not exist`() {
        val email = "nonexistent@example.com"
        whenever(userRepository.findByEmail(email)).thenReturn(null)

        val exception = assertThrows<UserNotFoundException> {
            userService.findByEmail(email)
        }
        assertEquals("User with email $email not found", exception.message)
        verify(userRepository).findByEmail(email)
    }


    @Test
    fun `findByUsername should return a user when username exists`() {
        val username = "john_doe"
        val user = User(
            username = username,
            email = "john.doe@example.com",
            password = "encoded_password",
            role = Role.USER,
            country = "US",
            salutation = "Mr."
        )
        whenever(userRepository.findByUsername(username)).thenReturn(user)

        val result = userService.findByUsername(username)

        assertNotNull(result)
        assertEquals(user.username, result.username)
        verify(userRepository).findByUsername(username)
    }

    @Test
    fun `findByUsername should throw UserNotFoundException when username does not exist`() {
        val username = "nonexistent_user"
        whenever(userRepository.findByUsername(username)).thenReturn(null)

        val exception = assertThrows<UserNotFoundException> {
            userService.findByUsername(username)
        }
        assertEquals("User with username $username not found", exception.message)
        verify(userRepository).findByUsername(username)
    }
}
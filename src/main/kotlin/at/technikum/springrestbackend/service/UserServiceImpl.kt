package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.PasswordUpdateDTO
import at.technikum.springrestbackend.dto.ProfileVisibilityDTO
import at.technikum.springrestbackend.dto.UserDTO
import at.technikum.springrestbackend.entity.User
import at.technikum.springrestbackend.exception.notFound.UserNotFoundException
import at.technikum.springrestbackend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserServiceImpl @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserService {

    @Transactional
    override fun createUser(userDTO: UserDTO): User {
        if (userRepository.findByUsername(userDTO.username) != null) {
            throw IllegalArgumentException("Username '${userDTO.username}' is already in use.")
        }
        if (userRepository.findByEmail(userDTO.email) != null) {
            throw IllegalArgumentException("Email '${userDTO.email}' is already in use.")
        }

        val user = User(
            username = userDTO.username,
            email = userDTO.email,
            password = passwordEncoder.encode(userDTO.password),
            role = userDTO.role,
            country = userDTO.country,
            salutation = userDTO.salutation
        )
        return userRepository.save(user)
    }

    override fun getUserById(id: UUID): User {
        if (!userRepository.existsById(id)) {
            throw UserNotFoundException("User with ID $id not found")
        }
        return userRepository.findById(id).get()
    }

    override fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    @Transactional
    override fun updateUser(id: UUID, userDTO: UserDTO): User {
        val existingUser = userRepository.findById(id).orElseThrow {
            UserNotFoundException("User with ID $id not found")
        }
        return existingUser.copy(
            username = userDTO.username,
            email = userDTO.email,
            password = passwordEncoder.encode(userDTO.password),
            role = userDTO.role,
            country = userDTO.country,
            salutation = userDTO.salutation
        ).also { userRepository.save(it) }
    }

    @Transactional
    override fun updatePassword(id: UUID, passwordUpdateDTO: PasswordUpdateDTO): User {
        val existingUser = userRepository.findById(id).orElseThrow {
            UserNotFoundException("User with ID $id not found")
        }

        // Validate the old password
        if (!passwordEncoder.matches(passwordUpdateDTO.oldPassword, existingUser.password)) {
            throw IllegalArgumentException("Old password is incorrect")
        }

        // Create a new User object with the updated password
        val updatedUser = existingUser.copy(
            password = passwordEncoder.encode(passwordUpdateDTO.newPassword),

        )

        return userRepository.save(updatedUser)
    }

    @Transactional
    override fun updateProfileVisibility(userId: UUID, profileVisibilityDTO: ProfileVisibilityDTO): User {

        val user = userRepository.findById(userId).orElseThrow {
            UserNotFoundException("User with ID $userId not found")
        }
        val updatedUser = user.copy(
            isPrivate = profileVisibilityDTO.isPrivate
        )
        return userRepository.save(updatedUser)
    }

    @Transactional
    override fun deleteUser(id: UUID) {
        if (!userRepository.existsById(id)) {
            throw UserNotFoundException("User with ID $id not found")
        }
        userRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun findByEmail(email: String): User {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException("User with email $email not found")
        return user
    }

    @Transactional(readOnly = true)
    override fun findByUsername(username: String): User {
        val user = userRepository.findByUsername(username)
            ?: throw UserNotFoundException("User with username $username not found")
        return user
    }
}

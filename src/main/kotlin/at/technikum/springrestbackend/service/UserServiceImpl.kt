package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.UpdateUserDTO
import at.technikum.springrestbackend.dto.UserDTO
import at.technikum.springrestbackend.entity.User
import at.technikum.springrestbackend.exception.notFound.UserNotFoundException
import at.technikum.springrestbackend.repository.FileRepository
import at.technikum.springrestbackend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.FileNotFoundException
import java.util.*

@Service
class UserServiceImpl @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val fileRepository: FileRepository
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
    override fun updateUser(id: UUID, userDTO: UpdateUserDTO): User {
        val existingUser = userRepository.findById(id).orElseThrow {
            UserNotFoundException("User with ID $id not found")
        }

        val file = userDTO.fileId?.let { fileId ->
            fileRepository.findById(fileId)
                .orElseThrow { FileNotFoundException("File with ID $fileId not found") }
        }

        val updatedUser = existingUser.copy(
            username = userDTO.username ?: existingUser.username,
            email = userDTO.email ?: existingUser.email,
            password = userDTO.password?.let { passwordEncoder.encode(it) } ?: existingUser.password,
            role = userDTO.role ?: existingUser.role,
            country = userDTO.country ?: existingUser.country,
            salutation = userDTO.salutation ?: existingUser.salutation,
            profilePicture = file,
            locked = userDTO.locked ?: existingUser.locked
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

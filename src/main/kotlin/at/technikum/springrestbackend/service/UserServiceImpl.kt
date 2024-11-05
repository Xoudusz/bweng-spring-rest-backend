package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.entity.User
import at.technikum.springrestbackend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserServiceImpl @Autowired constructor(
    private val userRepository: UserRepository
) : UserService {

    @Transactional
    override fun createUser(user: User): User {
        if (userRepository.findByUsername(user.username) != null || userRepository.findByEmail(user.email) != null) {
            throw IllegalArgumentException("Username or email already in use.")
        }
        return userRepository.save(user)
    }

    override fun getUserById(id: UUID): User? {
        return userRepository.findById(id).orElse(null)
    }

    override fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    @Transactional
    override fun updateUser(id: UUID, user: User): User? {
        return if (userRepository.existsById(id)) {
            user.id = id
            userRepository.save(user)
        } else {
            null
        }
    }

    @Transactional
    override fun deleteUser(id: UUID): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    @Transactional(readOnly = true)
    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    @Transactional(readOnly = true)
    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }
}

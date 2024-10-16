package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.entity.User
import at.technikum.springrestbackend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserService(@Autowired private val userRepository: UserRepository) {
    fun getUserById(id: Long): User? = userRepository.findById(id).orElse(null)
    fun createUser(user: User): User = userRepository.save(user)
}
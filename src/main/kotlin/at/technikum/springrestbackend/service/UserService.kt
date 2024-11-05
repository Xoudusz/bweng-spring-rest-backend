package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.entity.User
import org.springframework.stereotype.Service
import java.util.UUID

@Service
interface UserService {
    fun createUser(user: User): User
    fun getUserById(id: UUID): User?
    fun getAllUsers(): List<User>
    fun updateUser(id: UUID, user: User): User?
    fun deleteUser(id: UUID): Boolean
    fun findByEmail(email: String): User?
    fun findByUsername(username: String): User?
}

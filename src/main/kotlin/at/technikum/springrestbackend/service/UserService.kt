package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.UserDTO
import at.technikum.springrestbackend.entity.User
import org.springframework.stereotype.Service
import java.util.UUID

@Service
interface UserService {
    fun createUser(userDTO: UserDTO): User
    fun getUserById(id: UUID): User?
    fun getAllUsers(): List<User>
    fun updateUser(id: UUID, userDTO: UserDTO): User?
    fun deleteUser(id: UUID)
    fun findByEmail(email: String): User?
    fun findByUsername(username: String): User?
}

package at.technikum.springrestbackend.entity

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import java.util.*

class UserPrincipal(
    val id: UUID,
    username: String,
    password: String,
    private val role: String
) : User(username, password, listOf(SimpleGrantedAuthority(role))) {

    fun getRole(): String = role
}

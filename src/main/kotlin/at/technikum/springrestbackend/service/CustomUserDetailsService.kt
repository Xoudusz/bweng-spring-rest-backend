package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.entity.User
import at.technikum.springrestbackend.entity.UserPrincipal
import org.springframework.context.annotation.Lazy
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    @Lazy private val userService: UserService
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userService.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")

        return UserPrincipal(
            id = user.id,
            username = user.username,
            password = user.password,
            role = user.role.toString()
        )
    }
}

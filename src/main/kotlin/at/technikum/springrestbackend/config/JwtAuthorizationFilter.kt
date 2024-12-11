package at.technikum.springrestbackend.config

import at.technikum.springrestbackend.service.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthorizationFilter(
    private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwtCookie = request.cookies?.find { it.name == "JWT" }
        val token = jwtCookie?.value ?: request.getHeader("Authorization")?.substringAfter("Bearer ")

        if (token != null) {
            try {
                val username: String = tokenService.extractUsername(token)

                if (SecurityContextHolder.getContext().authentication == null) {
                    val userDetails: UserDetails = userDetailsService.loadUserByUsername(username)

                    if (tokenService.validateToken(token) && username == userDetails.username) {
                        val authToken = UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.authorities
                        )
                        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authToken

                        // Set the JWT token as an HttpOnly and Secure cookie
                        val cookie = Cookie("JWT", token).apply {
                            isHttpOnly = true
                            // isSecure = true // only used in HTTPS environments
                            path = "/"
                        }
                        response.addCookie(cookie)
                    }
                }
            } catch (ex: Exception) {
                response.writer.write(
                    """{"error": "Filter Authorization error:
                |${ex.message ?: "unknown error"}"}""".trimMargin()
                )
            }
        }

        filterChain.doFilter(request, response)
    }


}
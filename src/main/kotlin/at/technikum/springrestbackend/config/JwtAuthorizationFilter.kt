package at.technikum.springrestbackend.config

import at.technikum.springrestbackend.service.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthorizationFilter(
    private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService
) : OncePerRequestFilter() {

    public override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader: String? = request.getHeader("Authorization")

        // If Authorization header is missing, skip filtering
        if (authorizationHeader.isNullOrBlank()) {
            logger.info("No Authorization header found, skipping JWT filter")
            filterChain.doFilter(request, response)
            return
        }

        // Proceed only if the Authorization header starts with "Bearer "
        if (authorizationHeader.startsWith("Bearer ")) {
            val token = authorizationHeader.substringAfter("Bearer ")

            try {
                val username = tokenService.extractUsername(token)

                // Only authenticate if the SecurityContext has no authentication
                if (SecurityContextHolder.getContext().authentication == null) {
                    val userDetails = userDetailsService.loadUserByUsername(username)

                    // Ensure the token is valid
                    if (tokenService.isTokenValid(token)) {
                        val authToken = UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.authorities
                        )
                        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authToken
                    }
                }
            } catch (ex: Exception) {
                // Log the error and skip modifying the response status
                logger.warn("JWT validation failed: ${ex.message}")
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response)
    }
}

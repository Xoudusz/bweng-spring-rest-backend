package at.technikum.springrestbackend.config

import at.technikum.springrestbackend.service.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import java.io.PrintWriter

class JwtAuthorizationFilterTest {

    private lateinit var jwtAuthorizationFilter: JwtAuthorizationFilter
    private lateinit var userDetailsService: UserDetailsService
    private lateinit var tokenService: TokenService
    private lateinit var filterChain: FilterChain
    private lateinit var request: HttpServletRequest
    private lateinit var response: HttpServletResponse

    @BeforeEach
    fun setUp() {
        userDetailsService = mock(UserDetailsService::class.java)
        tokenService = mock(TokenService::class.java)
        jwtAuthorizationFilter = JwtAuthorizationFilter(userDetailsService, tokenService)
        filterChain = mock(FilterChain::class.java)
        request = mock(HttpServletRequest::class.java)
        response = mock(HttpServletResponse::class.java)
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should authenticate user when valid token is provided`() {
        // Arrange
        val token = "valid-token"
        val username = "testuser"
        val userDetails: UserDetails = User(username, "password", listOf())
        `when`(request.getHeader("Authorization")).thenReturn("Bearer $token")
        `when`(tokenService.extractUsername(token)).thenReturn(username)
        `when`(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails)

        // Act
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain)

        // Assert
        val auth = SecurityContextHolder.getContext().authentication
        assertNotNull(auth, "Authentication should be set in the SecurityContext")
        assertEquals(username, auth.name, "Authenticated username should match the token's username")
        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun `should skip authentication when no Authorization header is provided`() {
        // Arrange
        `when`(request.getHeader("Authorization")).thenReturn(null)

        // Act
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain)

        // Assert
        assertNull(SecurityContextHolder.getContext().authentication, "Authentication should not be set")
        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun `should skip authentication when Authorization header does not start with Bearer`() {
        // Arrange
        `when`(request.getHeader("Authorization")).thenReturn("InvalidTokenFormat")

        // Act
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain)

        // Assert
        assertNull(SecurityContextHolder.getContext().authentication, "Authentication should not be set")
        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun `should return unauthorized error when token is invalid`() {
        // Arrange
        val invalidToken = "invalid-token"
        `when`(request.getHeader("Authorization")).thenReturn("Bearer $invalidToken")
        `when`(tokenService.extractUsername(invalidToken)).thenThrow(RuntimeException("Invalid token"))

        val writer = mock(PrintWriter::class.java)
        `when`(response.writer).thenReturn(writer)

        // Act
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain)

        // Assert
        verify(response).status = HttpServletResponse.SC_UNAUTHORIZED
        verify(response).contentType = "application/json"
        verify(writer).write("""{"error": "Filter Authorization error: Invalid token"}""")
        verifyNoInteractions(filterChain)
    }


    @Test
    fun `should not overwrite existing authentication`() {
        // Arrange
        val token = "valid-token"
        val existingAuth = UsernamePasswordAuthenticationToken("existingUser", null, emptyList())
        SecurityContextHolder.getContext().authentication = existingAuth

        `when`(request.getHeader("Authorization")).thenReturn("Bearer $token")

        // Act
        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain)

        // Assert
        assertEquals(existingAuth, SecurityContextHolder.getContext().authentication, "Existing authentication should not be overwritten")
        verify(filterChain).doFilter(request, response)
    }

}

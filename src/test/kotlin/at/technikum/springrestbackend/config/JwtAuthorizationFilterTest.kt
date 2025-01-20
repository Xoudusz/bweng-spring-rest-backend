package at.technikum.springrestbackend.config

import at.technikum.springrestbackend.service.TokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
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
    private lateinit var userDetails: UserDetails

    @BeforeEach
    fun setUp() {
        userDetailsService = mock(UserDetailsService::class.java)
        tokenService = mock(TokenService::class.java)
        request = mock(HttpServletRequest::class.java)
        response = mock(HttpServletResponse::class.java)
        filterChain = mock(FilterChain::class.java)
        userDetails = mock(UserDetails::class.java)

        jwtAuthorizationFilter = JwtAuthorizationFilter(userDetailsService, tokenService)

        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should skip filter when authorization header is missing`() {
        `when`(request.getHeader("Authorization")).thenReturn(null)

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
        verifyNoInteractions(tokenService, userDetailsService)
    }

    @Test
    fun `should skip filter when authorization header does not start with Bearer`() {
        `when`(request.getHeader("Authorization")).thenReturn("InvalidHeader")

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain)

        verify(filterChain).doFilter(request, response)
        verifyNoInteractions(tokenService, userDetailsService)
    }

    @Test
    fun `should set authentication for valid token`() {
        val token = "validToken"
        val username = "user"
        val authorities = listOf(mock(org.springframework.security.core.GrantedAuthority::class.java))

        `when`(request.getHeader("Authorization")).thenReturn("Bearer $token")
        `when`(tokenService.extractUsername(token)).thenReturn(username)
        `when`(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails)
        `when`(userDetails.username).thenReturn(username)
        `when`(userDetails.authorities).thenReturn(authorities)

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain)

        val authentication = SecurityContextHolder.getContext().authentication as UsernamePasswordAuthenticationToken
        assert(authentication.name == username)
        assert(authentication.authorities == authorities)

        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun `should return unauthorized status when token service throws exception`() {
        val token = "invalidToken"
        val printWriter = mock(PrintWriter::class.java) // Mock the PrintWriter

        `when`(request.getHeader("Authorization")).thenReturn("Bearer $token")
        `when`(tokenService.extractUsername(token)).thenThrow(RuntimeException("Invalid token"))
        `when`(response.writer).thenReturn(printWriter) // Mock response.getWriter()

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain)

        verify(response).status = HttpServletResponse.SC_UNAUTHORIZED
        verify(response).contentType = "application/json"
        verify(printWriter).write("""{"error": "Filter Authorization error: Invalid token"}""") // Verify the write call
        verifyNoInteractions(userDetailsService)
        verifyNoMoreInteractions(filterChain)
    }

    @Test
    fun `should skip authentication if already set`() {
        val token = "validToken"
        val username = "user"

        `when`(request.getHeader("Authorization")).thenReturn("Bearer $token")
        `when`(tokenService.extractUsername(token)).thenReturn(username)
        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken("existingUser", null)

        jwtAuthorizationFilter.doFilterInternal(request, response, filterChain)

        verifyNoInteractions(userDetailsService)
        verify(filterChain).doFilter(request, response)
    }


}

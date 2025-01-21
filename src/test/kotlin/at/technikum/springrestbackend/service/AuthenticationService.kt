package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.entity.AuthenticationRequest
import at.technikum.springrestbackend.entity.AuthenticationResponse
import at.technikum.springrestbackend.entity.enums.Role
import at.technikum.springrestbackend.repository.RefreshTokenRepository
import at.technikum.springrestbackend.service.AuthenticationService
import at.technikum.springrestbackend.service.TokenService
import at.technikum.springrestbackend.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.InjectMocks
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.authentication.AuthenticationServiceException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import java.util.*

class AuthenticationServiceTest {

    @Mock
    private lateinit var authManager: AuthenticationManager

    @Mock
    private lateinit var userDetailsService: UserDetailsService

    @Mock
    private lateinit var tokenService: TokenService

    @Mock
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Mock
    private lateinit var userService: UserService

    @InjectMocks
    private lateinit var authenticationService: AuthenticationService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    //authenticationTests
    @Test
    fun `should authenticate user and return access and refresh tokens`() {
        // Arrange
        val authRequest = AuthenticationRequest("user@example.com", "password")
        val userDetails = User("username", "password", listOf())
        val foundUser = at.technikum.springrestbackend.entity.User(
            id = UUID.randomUUID(),
            username = "username",
            email = "user@example.com",
            password = "password",
            role = Role.USER,
            salutation = "Mr",
            country = "AUT"
        )
        val accessToken = "access-token"
        val refreshToken = "refresh-token"

        `when`(userService.findByEmail(authRequest.identifier)).thenReturn(foundUser)
        `when`(
            authManager.authenticate(
                UsernamePasswordAuthenticationToken(foundUser.username, authRequest.password)
            )
        ).thenReturn(null)
        `when`(userDetailsService.loadUserByUsername(foundUser.username)).thenReturn(userDetails)
        `when`(tokenService.generateToken(anyString(), anyString(), any(Date::class.java))).thenReturn(accessToken, refreshToken)

        // Act
        val response = authenticationService.authentication(authRequest)

        // Assert
        assertEquals(accessToken, response.accessToken)
        assertEquals(refreshToken, response.refreshToken)
        verify(refreshTokenRepository, times(1)).save(refreshToken, userDetails)
    }




































}

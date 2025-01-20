package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.entity.AuthenticationRequest
import at.technikum.springrestbackend.entity.AuthenticationResponse
import at.technikum.springrestbackend.entity.RefreshTokenRequest
import at.technikum.springrestbackend.entity.TokenResponse
import at.technikum.springrestbackend.service.AuthenticationService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class AuthControllerTest {

    @Mock
    private lateinit var authenticationService: AuthenticationService

    @InjectMocks
    private lateinit var authController: AuthController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should authenticate and return AuthenticationResponse`() {
        // Arrange: Input and mocked output
        val authRequest = AuthenticationRequest("user", "password")
        val authResponse = AuthenticationResponse("accessToken123", "refreshToken123")

        // Mock the service behavior
        `when`(authenticationService.authentication(authRequest)).thenReturn(authResponse)

        // Act: Call the controller method
        val response = authController.authenticate(authRequest)

        // Assert: Verify the response and interactions
        assertEquals("accessToken123", response.accessToken)
        assertEquals("refreshToken123", response.refreshToken)
        verify(authenticationService, times(1)).authentication(authRequest)
    }

    //checkTokenValidity

    @Test
    fun `should check token validity and return true`() {
        // Arrange
        val token = "Bearer valid-token"
        `when`(authenticationService.isTokenValid("valid-token")).thenReturn(true)

        // Act
        val isValid = authController.checkTokenValidity(token)

        // Assert
        assertTrue(isValid)
        verify(authenticationService, times(1)).isTokenValid("valid-token")
    }

    @Test
    fun `should check token validity and return false`() {
        // Arrange
        val token = "Bearer invalid-token"
        `when`(authenticationService.isTokenValid("invalid-token")).thenReturn(false)

        // Act
        val isValid = authController.checkTokenValidity(token)

        // Assert
        assertFalse(isValid)
        verify(authenticationService, times(1)).isTokenValid("invalid-token")
    }

    @Test
    fun `should refresh access token and return TokenResponse`() {
        // Arrange
        val refreshTokenRequest = RefreshTokenRequest("valid-refresh-token")
        val tokenResponse = TokenResponse("new-access-token")

        // Mock the service behavior
        `when`(authenticationService.refreshAccessToken(refreshTokenRequest.token)).thenReturn(tokenResponse.token)

        // Act
        val response = authController.refreshAccessToken(refreshTokenRequest)

        // Assert
        assertEquals("new-access-token", response.token)
        verify(authenticationService, times(1)).refreshAccessToken(refreshTokenRequest.token)
    }

    @Test
    fun `should propagate exception when refresh token is invalid`() {
        // Arrange
        val refreshTokenRequest = RefreshTokenRequest("invalid-refresh-token")
        `when`(authenticationService.refreshAccessToken(refreshTokenRequest.token))
            .thenThrow(IllegalArgumentException("Invalid refresh token"))

        // Act & Assert
        val exception = assertThrows<IllegalArgumentException> {
            authController.refreshAccessToken(refreshTokenRequest)
        }
        assertEquals("Invalid refresh token", exception.message)
        verify(authenticationService, times(1)).refreshAccessToken(refreshTokenRequest.token)
    }

    @Test
    fun `should handle null refresh token`() {
        // Arrange
        val refreshTokenRequest = RefreshTokenRequest(null.toString())
        `when`(authenticationService.refreshAccessToken(refreshTokenRequest.token))
            .thenThrow(IllegalArgumentException("Refresh token cannot be null"))

        // Act & Assert
        val exception = assertThrows<IllegalArgumentException> {
            authController.refreshAccessToken(refreshTokenRequest)
        }
        assertEquals("Refresh token cannot be null", exception.message)
        verify(authenticationService, times(1)).refreshAccessToken(refreshTokenRequest.token)
    }

    @Test
    fun `should handle empty refresh token`() {
        // Arrange
        val refreshTokenRequest = RefreshTokenRequest("")
        `when`(authenticationService.refreshAccessToken(refreshTokenRequest.token))
            .thenThrow(IllegalArgumentException("Refresh token cannot be empty"))

        // Act & Assert
        val exception = assertThrows<IllegalArgumentException> {
            authController.refreshAccessToken(refreshTokenRequest)
        }
        assertEquals("Refresh token cannot be empty", exception.message)
        verify(authenticationService, times(1)).refreshAccessToken(refreshTokenRequest.token)
    }

    @Test
    fun `should handle expired refresh token`() {
        // Arrange
        val refreshTokenRequest = RefreshTokenRequest("expired-refresh-token")
        `when`(authenticationService.refreshAccessToken(refreshTokenRequest.token))
            .thenThrow(IllegalArgumentException("Refresh token is expired"))

        // Act & Assert
        val exception = assertThrows<IllegalArgumentException> {
            authController.refreshAccessToken(refreshTokenRequest)
        }
        assertEquals("Refresh token is expired", exception.message)
        verify(authenticationService, times(1)).refreshAccessToken(refreshTokenRequest.token)
    }

    //logout
    @Test
    fun `should logout successfully`() {
        // Arrange
        val logoutRequest = AuthController.LogoutRequest("valid-token")

        // Act
        authController.logout(logoutRequest)

        // Assert
        verify(authenticationService, times(1)).logout(logoutRequest.token)
    }

    @Test
    fun `should propagate exception during logout`() {
        // Arrange
        val logoutRequest = AuthController.LogoutRequest("invalid-token")
        doThrow(IllegalArgumentException("Logout failed")).`when`(authenticationService).logout(logoutRequest.token)

        // Act & Assert
        val exception = assertThrows<IllegalArgumentException> {
            authController.logout(logoutRequest)
        }
        assertEquals("Logout failed", exception.message)
        verify(authenticationService, times(1)).logout(logoutRequest.token)
    }
}
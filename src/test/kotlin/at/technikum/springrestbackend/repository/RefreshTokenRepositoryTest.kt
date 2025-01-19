package at.technikum.springrestbackend.repository

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.security.core.userdetails.UserDetails

class RefreshTokenRepositoryTest {

    private lateinit var refreshTokenRepository: RefreshTokenRepository
    private lateinit var mockUserDetails: UserDetails

    @BeforeEach
    fun setUp() {
        refreshTokenRepository = RefreshTokenRepository()
        mockUserDetails = mock(UserDetails::class.java) // Mocked UserDetails for testing
    }



    @Test
    fun `should save and retrieve a userDetails by token`() {
        // Arrange
        val token = "test-token"

        // Act
        refreshTokenRepository.save(token, mockUserDetails)
        val retrievedUserDetails = refreshTokenRepository.findUserDetailsByToken(token)

        // Assert
        assertNotNull(retrievedUserDetails)
        assertEquals(mockUserDetails, retrievedUserDetails)
    }

    @Test
    fun `should return null for non-existent token`() {
        // Act
        val retrievedUserDetails = refreshTokenRepository.findUserDetailsByToken("non-existent-token")

        // Assert
        assertNull(retrievedUserDetails)
    }

    @Test
    fun `should delete a userDetails by token`() {
        // Arrange
        val token = "delete-token"
        refreshTokenRepository.save(token, mockUserDetails)

        // Act
        refreshTokenRepository.deleteByToken(token)
        val retrievedUserDetails = refreshTokenRepository.findUserDetailsByToken(token)

        // Assert
        assertNull(retrievedUserDetails)
    }

    @Test
    fun `should overwrite existing userDetails when saving with the same token`() {
        // Arrange
        val token = "overwrite-token"
        val newUserDetails = mock(UserDetails::class.java)
        refreshTokenRepository.save(token, mockUserDetails)

        // Act
        refreshTokenRepository.save(token, newUserDetails)
        val retrievedUserDetails = refreshTokenRepository.findUserDetailsByToken(token)

        // Assert
        assertNotNull(retrievedUserDetails)
        assertEquals(newUserDetails, retrievedUserDetails)
    }

    @Test
    fun `should handle deletion of non-existent token without error`() {
        // Arrange
        val token = "non-existent-token"

        // Act & Assert
        assertDoesNotThrow { refreshTokenRepository.deleteByToken(token) }
    }
}

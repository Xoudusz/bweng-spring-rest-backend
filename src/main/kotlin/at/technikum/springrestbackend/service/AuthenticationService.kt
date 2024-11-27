package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.entity.AuthenticationRequest
import at.technikum.springrestbackend.entity.AuthenticationResponse
import at.technikum.springrestbackend.entity.enums.Role
import at.technikum.springrestbackend.repository.RefreshTokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService,
    private val refreshTokenRepository: RefreshTokenRepository,
    @Value("\${jwt.accessTokenExpiration}") private val accessTokenExpiration: Long = 0,
    @Value("\${jwt.refreshTokenExpiration}") private val refreshTokenExpiration: Long = 0,
    private val userService: UserService
) {
    fun authentication(authenticationRequest: AuthenticationRequest): AuthenticationResponse {
        // Determine if identifier is an email or username
        val foundUser = when {
            authenticationRequest.identifier.contains("@") -> {
                userService.findByEmail(authenticationRequest.identifier)
            }
            else -> {
                userService.findByUsername(authenticationRequest.identifier)
            }
        }

        if (foundUser == null) {
            throw AuthenticationServiceException("User with identifier ${authenticationRequest.identifier} not found")
        }

       authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                foundUser.username,
                authenticationRequest.password
            )
        )

        val user = userDetailsService.loadUserByUsername(foundUser.username)

        val accessToken = createAccessToken(user, foundUser.role.toString())
        val refreshToken = createRefreshToken(user, foundUser.role.toString())

        refreshTokenRepository.save(refreshToken, user)

        return AuthenticationResponse(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    fun refreshAccessToken(refreshToken: String): String {
        val username = tokenService.extractUsername(refreshToken)

        val role = tokenService.extractRole(refreshToken)
            ?: throw AuthenticationServiceException("Role not found in the refresh token")

        return username.let { user ->
            val currentUserDetails = userDetailsService.loadUserByUsername(user)
            val refreshTokenUserDetails = refreshTokenRepository.findUserDetailsByToken(refreshToken)

            if (currentUserDetails.username == refreshTokenUserDetails?.username)
                createAccessToken(currentUserDetails, role)
            else
                throw AuthenticationServiceException("Invalid refresh token")
        }
    }

    fun isTokenValid(token: String): Boolean {
        return try {
            // Try to extract the claims and check the expiration date
            val claims = tokenService.extractAllClaims(token)
            val expiration = claims.expiration
            Date().before(expiration) // Returns true if the token is not expired
        } catch (e: Exception) {
            // If any exception occurs (e.g., token is invalid, signature mismatch), return false
            false
        }
    }

    // New method to logout (invalidate the refresh token)
    fun logout(refreshToken: String) {
        // Extract the username from the refresh token
        val username = tokenService.extractUsername(refreshToken)

        // Find the refresh token in the repository
        val refreshTokenUserDetails = refreshTokenRepository.findUserDetailsByToken(refreshToken)

        // If the refresh token is valid and associated with the user, remove it from the repository
        if (refreshTokenUserDetails?.username == username) {
            refreshTokenRepository.deleteByToken(refreshToken)
        } else {
            throw AuthenticationServiceException("Invalid refresh token")
        }
    }





    private fun createAccessToken(user: UserDetails, role: String): String {
        return tokenService.generateToken(
            subject = user.username,
            expiration = Date(System.currentTimeMillis() + accessTokenExpiration),
            role = role
        )
    }


    private fun createRefreshToken(user: UserDetails, role: String) = tokenService.generateToken(
        subject = user.username,
        expiration = Date(System.currentTimeMillis() + refreshTokenExpiration),
        role = role
    )


}
package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.entity.AuthenticationRequest
import at.technikum.springrestbackend.entity.AuthenticationResponse
import at.technikum.springrestbackend.entity.RefreshTokenRequest
import at.technikum.springrestbackend.entity.TokenResponse
import at.technikum.springrestbackend.service.AuthenticationService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationService: AuthenticationService
) {
    @PostMapping
    fun authenticate(
        @RequestBody authRequest: AuthenticationRequest
    ): AuthenticationResponse {
        println("AuthController.authenticate called with: $authRequest")
        val authentication = SecurityContextHolder.getContext().authentication
        println("Authentication: $authentication")
        return authenticationService.authentication(authRequest)
    }


    @PostMapping("/refresh")
    fun refreshAccessToken(
        @RequestBody request: RefreshTokenRequest
    ): TokenResponse = TokenResponse(token = authenticationService.refreshAccessToken(request.token))

    @GetMapping("/check")
    fun checkTokenValidity(
        @RequestParam("token") token: String
    ): Boolean {
        return authenticationService.isTokenValid(token)
    }


    @PostMapping("/logout")
    fun logout(
        @RequestBody request: LogoutRequest
    ) {
        authenticationService.logout(request.token)
    }


    data class LogoutRequest(val token: String)
}
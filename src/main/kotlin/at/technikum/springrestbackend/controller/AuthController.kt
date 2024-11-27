package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.entity.AuthenticationRequest
import at.technikum.springrestbackend.entity.AuthenticationResponse
import at.technikum.springrestbackend.entity.RefreshTokenRequest
import at.technikum.springrestbackend.entity.TokenResponse
import at.technikum.springrestbackend.service.AuthenticationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationService: AuthenticationService
) {
    @PostMapping
    fun authenticate(
        @RequestBody authRequest: AuthenticationRequest
    ): AuthenticationResponse =
        authenticationService.authentication(authRequest)

    @PostMapping("/refresh")
    fun refreshAccessToken(
        @RequestBody request: RefreshTokenRequest
    ): TokenResponse = TokenResponse(token = authenticationService.refreshAccessToken(request.token))

    @GetMapping("/check")
    fun checkTokenValidity(
        @RequestParam token: String
    ): Boolean = authenticationService.isTokenValid(token)

    @PostMapping("/logout")
    fun logout(
        @RequestParam token: String
    ) {
        authenticationService.logout(token)
    }
}
package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.entity.AuthenticationRequest
import at.technikum.springrestbackend.entity.AuthenticationResponse
import at.technikum.springrestbackend.entity.RefreshTokenRequest
import at.technikum.springrestbackend.entity.TokenResponse
import at.technikum.springrestbackend.service.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
        @RequestHeader("Authorization") authorizationHeader: String
    ): Boolean {
        val token = authorizationHeader.removePrefix("Bearer ").trim()
        return authenticationService.isTokenValid(token)
    }


    @PostMapping("/logout")
    fun logout(
        @RequestBody request: LogoutRequest
    ) {
        authenticationService.logout(request.token)
    }


    data class LogoutRequest(val token: String)
    
    @PostMapping("/logout")
    fun logout(@RequestBody request: RefreshTokenRequest): ResponseEntity<Void> {
        authenticationService.logout(request.token)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/check")
    fun checkToken(@RequestHeader("Authorization") token: String): ResponseEntity<String> {
        val role = authenticationService.checkToken(token)
        return ResponseEntity(role, HttpStatus.OK)
    }
}
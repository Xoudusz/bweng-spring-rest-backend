package at.technikum.springrestbackend.entity

data class AuthenticationRequest(
    val username: String,
    val password: String,
)

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String,
)

data class RefreshTokenRequest(
    val token: String
)

data class TokenResponse(
    val token: String
)
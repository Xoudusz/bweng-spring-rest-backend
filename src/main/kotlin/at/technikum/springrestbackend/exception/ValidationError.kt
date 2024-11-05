package at.technikum.springrestbackend.exception

data class ValidationError(
    val field: String,
    val message: String
)
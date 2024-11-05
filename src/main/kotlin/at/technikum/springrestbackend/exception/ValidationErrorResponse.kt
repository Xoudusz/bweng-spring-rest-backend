package at.technikum.springrestbackend.exception

data class ValidationErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val errors: List<ValidationError>
)

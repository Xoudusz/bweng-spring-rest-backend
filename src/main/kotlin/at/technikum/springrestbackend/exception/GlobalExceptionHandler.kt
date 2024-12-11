package at.technikum.springrestbackend.exception

import at.technikum.springrestbackend.exception.notFound.FollowNotFoundException
import at.technikum.springrestbackend.exception.notFound.NotificationNotFoundException
import at.technikum.springrestbackend.exception.notFound.PostNotFoundException
import at.technikum.springrestbackend.exception.notFound.UserNotFoundException
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import java.security.SignatureException
import javax.naming.AuthenticationException

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(value = [ExpiredJwtException::class, AuthenticationException::class, SignatureException::class])
    fun handleAuthenticationExceptions(ex: RuntimeException): ResponseEntity<String> {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body("Authentication failed: ${ex.message}")
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgument(
        ex: IllegalArgumentException, request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Bad Request",
            message = ex.message ?: "Invalid input",
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException, request: HttpServletRequest
    ): ResponseEntity<ValidationErrorResponse> {
        val errors = ex.bindingResult.fieldErrors.map { fieldError ->
            ValidationError(
                field = fieldError.field, message = fieldError.defaultMessage ?: "Invalid value"
            )
        }

        val validationErrorResponse = ValidationErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Failed",
            message = "Input validation failed for one or more fields.",
            path = request.requestURI,
            errors = errors
        )

        return ResponseEntity(validationErrorResponse, HttpStatus.BAD_REQUEST)
    }


    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(
        ex: UserNotFoundException, request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "User Not Found",
            message = ex.message ?: "The specified user was not found.",
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(PostNotFoundException::class)
    fun handlePostNotFound(
        ex: PostNotFoundException, request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Post Not Found",
            message = ex.message ?: "The specified post was not found.",
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(FollowNotFoundException::class)
    fun followPostNotFound(
        ex: FollowNotFoundException, request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Follower Not Found",
            message = ex.message ?: "The specified follower was not found.",
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(NotificationNotFoundException::class)
    fun handleNotificationNotFound(
        ex: NotificationNotFoundException, request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.NOT_FOUND.value(),
            error = "Notification Not Found",
            message = ex.message ?: "The specified notification was not found.",
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(IllegalStateException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleIllegalStateException(
        ex: IllegalStateException,
        request: HttpServletRequest
        ): ResponseEntity<ErrorResponse>{
        val errorResponse = ErrorResponse(
            status = HttpStatus.CONFLICT.value(),
            error = "Conflict",
            message = ex.message ?: "An unexpected state occurred",
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(FileException::class)
    fun handleFileException(
        ex: FileException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "File Handling Error",
            message = ex.message ?: "An error occurred while processing the file.",
            path = request.requestURI
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }


}
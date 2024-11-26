package at.technikum.springrestbackend.dto.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [RoleValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidRole(
    val message: String = "Invalid role. Accepted values are USER and ADMIN",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)


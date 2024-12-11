package at.technikum.springrestbackend.dto.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [CountryCodeValidator::class])
annotation class ValidCountryCode(
    val message: String = "Invalid country code.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
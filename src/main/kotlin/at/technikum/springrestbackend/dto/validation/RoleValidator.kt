package at.technikum.springrestbackend.dto.validation

import at.technikum.springrestbackend.entity.enums.Role
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class RoleValidator : ConstraintValidator<ValidRole, Role> {
    override fun isValid(value: Role?, context: ConstraintValidatorContext): Boolean {
        // Allow null values (validation passes if the field is null)
        if (value == null) return true

        // Validate non-null values
        return Role.entries.contains(value)
    }
}
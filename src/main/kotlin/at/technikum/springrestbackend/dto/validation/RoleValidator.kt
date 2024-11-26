package at.technikum.springrestbackend.dto.validation

import at.technikum.springrestbackend.entity.enums.Role
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class RoleValidator : ConstraintValidator<ValidRole, Role> {
    override fun isValid(value: Role?, context: ConstraintValidatorContext): Boolean {
        return value != null && Role.entries.toTypedArray().contains(value)
    }
}
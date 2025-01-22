package at.technikum.springrestbackend.dto.validation

import com.neovisionaries.i18n.CountryCode
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext


class CountryCodeValidator : ConstraintValidator<ValidCountryCode, String> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        // Allow null values (validation passes if the field is null)
        if (value == null) return true

        // Check if the value is blank
        if (value.isBlank()) {
            return false
        }

        return try {
            // Attempt to retrieve CountryCode enum by its two-letter code
            CountryCode.getByCode(value.uppercase(), false) != null
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}
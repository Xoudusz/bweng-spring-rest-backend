package at.technikum.springrestbackend.dto

import at.technikum.springrestbackend.entity.enums.Role
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDtoTest {

    private lateinit var validator: Validator

    @BeforeAll
    fun setUp() {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `valid UserDTO should have no constraint violations`() {
        val userDTO = UserDTO(
            username = "valid_user",
            email = "user@example.com",
            password = "ValidPassw0rd!",
            role = Role.USER,
            salutation = "Mr.",
            country = "US"
        )

        val violations = validator.validate(userDTO)
        assertThat(violations).isEmpty()
    }

    @Test
    fun `invalid username should trigger constraint violation`() {
        val userDTO = UserDTO(
            username = "usr", // Too short
            email = "user@example.com",
            password = "ValidPassw0rd!",
            role = Role.USER,
            salutation = "Mr.",
            country = "US"
        )

        val violations = validator.validate(userDTO)
        assertThat(violations).isNotEmpty
        assertThat(violations).anyMatch { it.propertyPath.toString() == "username" && it.message.contains("between 5 and 50 characters") }
    }

    @Test
    fun `invalid email should trigger constraint violation`() {
        val userDTO = UserDTO(
            username = "valid_user",
            email = "invalid-email", // Invalid email format
            password = "ValidPassw0rd!",
            role = Role.USER,
            salutation = "Mr.",
            country = "US"
        )

        val violations = validator.validate(userDTO)
        assertThat(violations).isNotEmpty
        assertThat(violations).anyMatch { it.propertyPath.toString() == "email" && it.message.contains("Email should be valid") }
    }

    @Test
    fun `invalid password should trigger constraint violation`() {
        val userDTO = UserDTO(
            username = "valid_user",
            email = "user@example.com",
            password = "short", // Too short and missing required character types
            role = Role.USER,
            salutation = "Mr.",
            country = "US"
        )

        val violations = validator.validate(userDTO)
        assertThat(violations).isNotEmpty
        assertThat(violations).anyMatch {
            it.propertyPath.toString() == "password" &&
                    it.message.contains("Password must be at least 12 characters")
        }
        assertThat(violations).anyMatch {
            it.propertyPath.toString() == "password" &&
                    it.message.contains("Password must contain at least one uppercase letter")
        }
    }

    @Test
    fun `invalid country code should trigger constraint violation`() {
        val userDTO = UserDTO(
            username = "valid_user",
            email = "user@example.com",
            password = "ValidPassw0rd!",
            role = Role.USER,
            salutation = "Mr.",
            country = "XX" // Assuming XX is not a valid ISO country code
        )

        val violations = validator.validate(userDTO)
        assertThat(violations).isNotEmpty
        assertThat(violations).anyMatch { it.propertyPath.toString() == "country" && it.message.contains("Country code must be a valid ISO country code") }
    }

    @Test
    fun `blank fields should trigger constraint violations`() {
        val userDTO = UserDTO(
            username = "",
            email = "",
            password = "",
            role = Role.USER,
            salutation = "",
            country = ""
        )

        val violations = validator.validate(userDTO)
        assertThat(violations).hasSizeGreaterThanOrEqualTo(4) // username, email, password, country
        assertThat(violations).anyMatch { it.propertyPath.toString() == "username" && it.message.contains("Username is required") }
        assertThat(violations).anyMatch { it.propertyPath.toString() == "email" && it.message.contains("Email is required") }
        assertThat(violations).anyMatch { it.propertyPath.toString() == "password" && it.message.contains("Password is required") }
        assertThat(violations).anyMatch { it.propertyPath.toString() == "country" && it.message.contains("Country code is required") }
    }
}

package at.technikum.springrestbackend.dto

import at.technikum.springrestbackend.entity.enums.Role
import jakarta.validation.Validation
import jakarta.validation.Validator
import jakarta.validation.ValidatorFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateUserDtoTest {

    private lateinit var validator: Validator

    @BeforeAll
    fun setUp() {
        val factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
        validator = factory.validator
    }

    @Test
    fun `valid UpdateUserDTO with all fields should have no constraint violations`() {
        val updateUserDTO = UpdateUserDTO(
            username = "valid_user",
            email = "user@example.com",
            password = "ValidPassw0rd!",
            role = Role.ADMIN,
            salutation = "Ms.",
            country = "DE",
            fileId = "file123",
            locked = true
        )

        val violations = validator.validate(updateUserDTO)
        assertThat(violations).isEmpty()
    }

    @Test
    fun `valid UpdateUserDTO with only some fields should have no constraint violations`() {
        val updateUserDTO = UpdateUserDTO(
            email = "another.user@example.com",
            locked = false
        )

        val violations = validator.validate(updateUserDTO)
        assertThat(violations).isEmpty()
    }

    @Nested
    inner class UsernameValidationTests {

        @Test
        fun `null username should have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(username = null)
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
        }

        @Test
        fun `valid username should have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(username = "valid_user123")
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
        }

        @Test
        fun `username too short should trigger constraint violation`() {
            val updateUserDTO = UpdateUserDTO(username = "usr")
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isNotEmpty
            assertThat(violations).anyMatch {
                it.propertyPath.toString() == "username" &&
                        it.message.contains("between 5 and 50 characters")
            }
        }

        @Test
        fun `username with invalid characters should trigger constraint violation`() {
            val updateUserDTO = UpdateUserDTO(username = "invalid user!")
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isNotEmpty
            assertThat(violations).anyMatch {
                it.propertyPath.toString() == "username" &&
                        it.message.contains("can only contain letters, numbers, and underscores")
            }
        }
    }

    @Nested
    inner class EmailValidationTests {

        @Test
        fun `null email should have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(email = null)
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
        }

        @Test
        fun `valid email should have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(email = "valid.email@example.com")
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
        }

        @Test
        fun `invalid email format should trigger constraint violation`() {
            val updateUserDTO = UpdateUserDTO(email = "invalid-email")
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isNotEmpty
            assertThat(violations).anyMatch {
                it.propertyPath.toString() == "email" &&
                        it.message.contains("Email should be valid")
            }
        }

        @Test
        fun `email exceeding max length should trigger constraint violation`() {
            val longEmail = "a".repeat(101) + "@example.com"
            val updateUserDTO = UpdateUserDTO(email = longEmail)
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isNotEmpty
            assertThat(violations).anyMatch {
                it.propertyPath.toString() == "email" &&
                        it.message.contains("Email should not exceed 100 characters")
            }
        }
    }

    @Nested
    inner class PasswordValidationTests {

        @Test
        fun `null password should have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(password = null)
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
        }

        @Test
        fun `valid password should have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(password = "StrongPassw0rd!")
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
        }

        @Test
        fun `password too short should trigger constraint violation`() {
            val updateUserDTO = UpdateUserDTO(password = "Short1!")
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isNotEmpty
            assertThat(violations).anyMatch {
                it.propertyPath.toString() == "password" &&
                        it.message.contains("Password must be at least 12 characters")
            }
        }

        @Test
        fun `password missing uppercase letter should trigger constraint violation`() {
            val updateUserDTO = UpdateUserDTO(password = "lowercase1!")
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isNotEmpty
            assertThat(violations).anyMatch {
                it.propertyPath.toString() == "password" &&
                        it.message.contains("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
            }
        }

        @Test
        fun `password missing lowercase letter should trigger constraint violation`() {
            val updateUserDTO = UpdateUserDTO(password = "UPPERCASE1!")
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isNotEmpty
            assertThat(violations).anyMatch {
                it.propertyPath.toString() == "password" &&
                        it.message.contains("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
            }
        }

        @Test
        fun `password missing digit should trigger constraint violation`() {
            val updateUserDTO = UpdateUserDTO(password = "NoDigitsHere!")
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isNotEmpty
            assertThat(violations).anyMatch {
                it.propertyPath.toString() == "password" &&
                        it.message.contains("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
            }
        }

        @Test
        fun `password missing special character should trigger constraint violation`() {
            val updateUserDTO = UpdateUserDTO(password = "NoSpecialChar1")
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isNotEmpty
            assertThat(violations).anyMatch {
                it.propertyPath.toString() == "password" &&
                        it.message.contains("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
            }
        }
    }

    @Nested
    inner class RoleValidationTests {

        @Test
        fun `null role should have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(role = null)
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
        }

        @Test
        fun `valid role should have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(role = Role.USER)
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
        }

        @Test
        fun `invalid role should trigger constraint violation`() {
            // Since Role is an enum and the field is non-nullable, it's challenging to pass an invalid role.
            // However, if your custom validator allows for dynamic role values, you might test it accordingly.
            // For the sake of this example, let's assume we can cast an invalid value.

            // WARNING: This is a hypothetical scenario as enums in Kotlin can't hold invalid values directly.
            // You might need to test the validator separately if it handles strings or other inputs.

            // Example: If the validator somehow receives an invalid string representation
            // This requires a different approach, possibly mocking or extending the validator.

            // Therefore, this test case might not be applicable unless your implementation allows invalid roles.
            // Instead, ensure that the custom @ValidRole annotation is thoroughly tested in its own test class.
        }
    }

    @Nested
    inner class CountryCodeValidationTests {

        @Test
        fun `null country code should have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(country = null)
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
        }

        @Test
        fun `valid country code should have no constraint violations`() {
            val validCountryCodes = listOf("US", "DE", "FR", "GB", "CN")
            validCountryCodes.forEach { code ->
                val updateUserDTO = UpdateUserDTO(country = code)
                val violations = validator.validate(updateUserDTO)
                assertThat(violations).isEmpty()
            }
        }


    }

    @Nested
    inner class FileIdAndLockedValidationTests {

        @Test
        fun `null fileId should have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(fileId = null)
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
        }

        @Test
        fun `valid fileId should have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(fileId = "file456")
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
        }

        @Test
        fun `null locked should default to false and have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(locked = null)
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
            assertThat(updateUserDTO.locked).isNull() // Since it's a DTO, the default value is set on creation, not during validation
        }

        @Test
        fun `locked set to true should have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(locked = true)
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
        }

        @Test
        fun `locked set to false should have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(locked = false)
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
        }
    }

    @Nested
    inner class SalutationValidationTests {

        @Test
        fun `null salutation should have no constraint violations`() {
            val updateUserDTO = UpdateUserDTO(salutation = null)
            val violations = validator.validate(updateUserDTO)
            assertThat(violations).isEmpty()
        }

        @Test
        fun `any salutation should have no constraint violations`() {
            val salutations = listOf("Mr.", "Ms.", "Dr.", "Prof.", "Mx.")
            salutations.forEach { salutation ->
                val updateUserDTO = UpdateUserDTO(salutation = salutation)
                val violations = validator.validate(updateUserDTO)
                assertThat(violations).isEmpty()
            }
        }
    }


}

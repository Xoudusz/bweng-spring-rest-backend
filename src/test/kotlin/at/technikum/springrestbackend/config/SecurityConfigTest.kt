package at.technikum.springrestbackend.config

import at.technikum.springrestbackend.entity.User
import at.technikum.springrestbackend.repository.UserRepository
import at.technikum.springrestbackend.service.JwtUserDetailsService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.capture
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val passwordEncoder: PasswordEncoder = mock(PasswordEncoder::class.java)
    private val userDetailsService = JwtUserDetailsService(userRepository)
    private val securityConfig = SecurityConfig()

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `should authenticate with valid credentials`() {
        // Arrange
        val daoAuthenticationProvider = DaoAuthenticationProvider().apply {
            setUserDetailsService(userDetailsService)
            setPasswordEncoder(passwordEncoder)
        }

        val username = "testuser"
        val rawPassword = "password123"
        val encodedPassword = "encodedPassword123"

        val userEntity = User(
            username = username,
            email = "test@example.com",
            password = encodedPassword,
            role = at.technikum.springrestbackend.entity.enums.Role.USER,
            salutation = "Mr.",
            country = "AUT"
        )

        `when`(userRepository.findByUsername(username)).thenReturn(userEntity)
        `when`(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true)

        // Act
        val authentication = daoAuthenticationProvider.authenticate(
            UsernamePasswordAuthenticationToken(username, rawPassword)
        )

        // Assert
        assertTrue(authentication.isAuthenticated, "Authentication should be successful")
    }

    @Test
    fun `should fail authentication with invalid credentials`() {
        // Arrange
        val daoAuthenticationProvider = DaoAuthenticationProvider().apply {
            setUserDetailsService(userDetailsService)
            setPasswordEncoder(passwordEncoder)
        }

        val username = "testuser"
        val rawPassword = "wrongPassword"
        val encodedPassword = "encodedPassword123"

        val userEntity = User(
            username = username,
            email = "test@example.com",
            password = encodedPassword,
            role = at.technikum.springrestbackend.entity.enums.Role.USER,
            salutation = "Mr.",
            country = "AUT"
        )

        `when`(userRepository.findByUsername(username)).thenReturn(userEntity)
        `when`(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false)

        // Act & Assert
        val exception = org.junit.jupiter.api.assertThrows<BadCredentialsException> {
            daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken(username, rawPassword))
        }

        assertTrue(
            exception.message?.contains("Bad credentials") == true,
            "Exception message should indicate bad credentials"
        )
    }

    @Test
    fun `userDetailsService should return JwtUserDetailsService with userRepository`() {
        // Arrange
        val mockUserRepository: UserRepository = mock(UserRepository::class.java)
        val securityConfig = SecurityConfig()

        // Act
        val userDetailsService = securityConfig.userDetailsService(mockUserRepository)

        // Assert
        assertTrue(userDetailsService is JwtUserDetailsService, "Returned service should be JwtUserDetailsService")
    }

    @Test
    fun `authenticationManager should return AuthenticationManager from configuration`() {
        // Arrange
        val mockAuthenticationConfiguration: AuthenticationConfiguration = mock(AuthenticationConfiguration::class.java)
        val mockAuthenticationManager: AuthenticationManager = mock(AuthenticationManager::class.java)
        `when`(mockAuthenticationConfiguration.authenticationManager).thenReturn(mockAuthenticationManager)
        val securityConfig = SecurityConfig()

        // Act
        val authenticationManager = securityConfig.authenticationManager(mockAuthenticationConfiguration)

        // Assert
        assertEquals(mockAuthenticationManager, authenticationManager, "AuthenticationManager should match the mock")
    }

    @Test
    fun `authenticationProvider should return properly configured DaoAuthenticationProvider`() {
        // Arrange
        val mockUserRepository: UserRepository = mock(UserRepository::class.java)
        val securityConfig = SecurityConfig()
        val expectedPasswordEncoder: PasswordEncoder = securityConfig.encoder()
        val expectedUserDetailsService = securityConfig.userDetailsService(mockUserRepository)

        // Act
        val authenticationProvider = securityConfig.authenticationProvider(mockUserRepository)

        // Assert
        assertTrue(authenticationProvider is DaoAuthenticationProvider, "AuthenticationProvider should be DaoAuthenticationProvider")
        val provider = authenticationProvider as DaoAuthenticationProvider

        // Use reflection to access private fields
        val passwordEncoderField = DaoAuthenticationProvider::class.java.getDeclaredField("passwordEncoder")
        passwordEncoderField.isAccessible = true
        val actualPasswordEncoder = passwordEncoderField.get(provider)

        val userDetailsServiceField = DaoAuthenticationProvider::class.java.getDeclaredField("userDetailsService")
        userDetailsServiceField.isAccessible = true
        val actualUserDetailsService = userDetailsServiceField.get(provider)

        // Assertions
        assertNotNull(actualPasswordEncoder, "PasswordEncoder should not be null")
        assertNotNull(actualUserDetailsService, "UserDetailsService should not be null")

        // Functional equivalence checks
        val rawPassword = "password123"
        val encodedPassword = (actualPasswordEncoder as PasswordEncoder).encode(rawPassword)
        assertTrue(actualPasswordEncoder.matches(rawPassword, encodedPassword), "PasswordEncoder should match functionality")

        assertEquals(expectedUserDetailsService::class, actualUserDetailsService::class, "UserDetailsService class types should match")
    }

    @Test
    fun `securityFilterChain should configure HttpSecurity correctly`() {
        // Arrange
        val mockHttpSecurity: HttpSecurity = mock(HttpSecurity::class.java)
        val mockJwtAuthorizationFilter: JwtAuthorizationFilter = mock(JwtAuthorizationFilter::class.java)
        val mockAuthenticationProvider: AuthenticationProvider = mock(AuthenticationProvider::class.java)
        val mockSecurityFilterChain: DefaultSecurityFilterChain = mock(DefaultSecurityFilterChain::class.java)

        // Mock behavior for HttpSecurity methods
        `when`(mockHttpSecurity.csrf(any())).thenReturn(mockHttpSecurity)
        `when`(mockHttpSecurity.formLogin(any())).thenReturn(mockHttpSecurity)
        `when`(mockHttpSecurity.cors(any())).thenReturn(mockHttpSecurity)
        `when`(mockHttpSecurity.authorizeHttpRequests(any())).thenReturn(mockHttpSecurity)
        `when`(mockHttpSecurity.sessionManagement(any())).thenReturn(mockHttpSecurity)
        `when`(mockHttpSecurity.authenticationProvider(mockAuthenticationProvider)).thenReturn(mockHttpSecurity)
        `when`(mockHttpSecurity.addFilterBefore(mockJwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java))
            .thenReturn(mockHttpSecurity)
        `when`(mockHttpSecurity.build()).thenReturn(mockSecurityFilterChain)

        val securityConfig = SecurityConfig()

        // Act
        val filterChain = securityConfig.securityFilterChain(
            http = mockHttpSecurity,
            jwtAuthenticationFilter = mockJwtAuthorizationFilter,
            authenticationProvider = mockAuthenticationProvider
        )

        // Assert
        assertNotNull(filterChain, "SecurityFilterChain should not be null")
        verify(mockHttpSecurity).csrf(any())
        verify(mockHttpSecurity).formLogin(any())
        verify(mockHttpSecurity).cors(any())
        verify(mockHttpSecurity).authorizeHttpRequests(any())
        verify(mockHttpSecurity).sessionManagement(any())
        verify(mockHttpSecurity).authenticationProvider(mockAuthenticationProvider)
        verify(mockHttpSecurity).addFilterBefore(mockJwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    @Test
    fun `encoder should return a BCryptPasswordEncoder instance`() {
        // Arrange
        val securityConfig = SecurityConfig()

        // Act
        val passwordEncoder = securityConfig.encoder()

        // Assert
        assertTrue(passwordEncoder is BCryptPasswordEncoder, "PasswordEncoder should be BCryptPasswordEncoder")
    }

    @Test
    fun `corsConfigurationSource should configure CORS correctly`() {
        // Arrange
        val securityConfig = SecurityConfig()
        val corsSource = securityConfig.corsConfigurationSource()

        // Act
        val corsConfig = corsSource.corsConfigurations["/api/**"]

        // Assert
        assertNotNull(corsConfig, "CORS configuration should not be null")
        assertEquals(listOf("http://localhost:8081"), corsConfig?.allowedOrigins, "Allowed origins should match")
        assertEquals(listOf("*"), corsConfig?.allowedMethods, "Allowed methods should match")
        assertEquals(listOf("*"), corsConfig?.allowedHeaders, "Allowed headers should match")
        assertTrue(corsConfig?.allowCredentials == true, "CORS should allow credentials")
    }


    @Test
    fun `should handle swagger redirection`() {
        mockMvc.get("/swagger.html").andExpect {
            status { isFound() } // 302 Redirect
            header { string("Location", "/swagger-ui/index.html") }
        }
    }

    @Test
    fun `should allow access to swagger UI`() {
        mockMvc.get("/swagger-ui/index.html").andExpect {
            status { isOk() }
            content { contentType("text/html") }
        }
    }

    @Test
    fun `should allow access to openapi docs`() {
        mockMvc.get("/v3/api-docs").andExpect {
            status { isOk() }
            content { contentType("application/json") }
        }
    }

    @Test
    fun `should secure non-permitted endpoints`() {
        mockMvc.get("/secure-endpoint").andExpect {
            status { isForbidden() }
        }
    }





















}

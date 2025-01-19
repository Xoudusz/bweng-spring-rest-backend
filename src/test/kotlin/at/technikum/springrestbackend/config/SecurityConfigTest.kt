package at.technikum.springrestbackend.config

import jakarta.servlet.Filter
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.reset
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.FilterChainProxy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext


@SpringBootTest(properties = ["spring.profiles.active=test"])
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var authenticationProvider: AuthenticationProvider

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var filterChainProxy: FilterChainProxy

    @MockBean
    private lateinit var jwtAuthorizationFilter: JwtAuthorizationFilter

    @BeforeEach
    fun setUp(@Autowired webApplicationContext: WebApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()

    }

    @BeforeEach
    fun resetMocks() {
        reset(jwtAuthorizationFilter)
    }

    @BeforeEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    //SecurityConfigBeanTests
    @Test
    fun `should configure authentication manager`() {
        assertNotNull(authenticationManager)
    }

    @Test
    fun `should configure authentication provider`() {
        assertNotNull(authenticationProvider)
    }

    @Test
    fun `should configure user details service`() {
        assertNotNull(userDetailsService)
    }

    @Test
    fun `should configure password encoder`() {
        assertNotNull(passwordEncoder)
    }

    //SecurityConfigTests
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

    //TODO fix this test
    @Test
    fun `should secure non-permitted endpoints`() {
        mockMvc.get("/api/users")
            .andDo { print() } // This will print the request/response details using MockMvc's built-in printing.
            .andExpect {
                status { isForbidden() }
            }
    }

    //CorsConfigTests
    //TODO fix this test
    @Test
    fun `should allow requests from allowed origins`() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .options("/api/test-cors")
                .header(HttpHeaders.ORIGIN, "http://localhost:8081")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:8081"))
    }

    @Test
    fun `should reject requests from disallowed origins`() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .options("/api/test-cors")
                .header(HttpHeaders.ORIGIN, "https://unauthorized-origin.com")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, HttpMethod.GET)
        ).andExpect(MockMvcResultMatchers.status().isForbidden)
    }

    //JwtFilterIntegrationTests
    @Test
    fun `should accept requests when JWT filter allows`() {
        doNothing().`when`(jwtAuthorizationFilter).doFilter(any(), any(), any())

        mockMvc.get("/api/posts") {
            header("Authorization", "Bearer mock-token")
        }.andExpect {
            status { isOk() }
        }
    }

    //JwtFilterOrderTests
    @Test
    fun `should include JwtAuthorizationFilter in the filter chain`() {
        val filters: List<Filter> = filterChainProxy.getFilters("/api/secure-endpoint")

        val jwtFilterIndex = filters.indexOfFirst { it is JwtAuthorizationFilter }

        assertTrue(jwtFilterIndex >= 0) { "JwtAuthorizationFilter is not present in the filter chain" }
    }

    //SessionManagementTests

    //PathAuthorizationTests























}

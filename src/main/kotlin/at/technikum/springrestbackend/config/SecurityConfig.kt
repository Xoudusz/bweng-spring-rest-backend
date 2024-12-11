package at.technikum.springrestbackend.config

import at.technikum.springrestbackend.repository.UserRepository
import at.technikum.springrestbackend.service.JwtUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig {

    @Bean
    fun userDetailsService(userRepository: UserRepository): UserDetailsService =
        JwtUserDetailsService(userRepository)

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    @Bean
    fun authenticationProvider(userRepository: UserRepository): AuthenticationProvider =
        DaoAuthenticationProvider()
            .also {
                it.setUserDetailsService(userDetailsService(userRepository))
                it.setPasswordEncoder(encoder())
            }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthorizationFilter, // Custom JWT filter
        authenticationProvider: AuthenticationProvider
    ): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .formLogin{it.disable()}
            .cors{it.configurationSource(corsConfigurationSource())}
            .authorizeHttpRequests {
                it
                    .requestMatchers(
                        "/v3/api-docs/**", "/swagger-ui/**", "/swagger.html", // Allow Swagger paths
                        "/api/auth/**", "/error",           // Allow authentication endpoints
                        "/api/users" // Allow user creation so we can get a jwt token (for testing)
                    ).permitAll()
                    .anyRequest().fullyAuthenticated() // Secure other endpoints
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless sessions for JWT
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): UrlBasedCorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("http://localhost:8081")
        config.allowedMethods = listOf("*")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true
        source.registerCorsConfiguration("/api/**", config)
        return source
    }
}

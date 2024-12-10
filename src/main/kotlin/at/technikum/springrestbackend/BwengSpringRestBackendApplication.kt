package at.technikum.springrestbackend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@EnableWebSecurity
@SpringBootApplication
@ConfigurationPropertiesScan
class BwengSpringRestBackendApplication

fun main(args: Array<String>) {
	runApplication<BwengSpringRestBackendApplication>(*args)
}

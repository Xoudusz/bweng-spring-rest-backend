package at.technikum.springrestbackend.property

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.minio")
data class MinioProperties(
    val url: String,
    val port: Int,
    val user: String,
    val password: String,
    val bucket: String
)

package at.technikum.springrestbackend.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "minio")
data class MinioProperties(
    var url: String = "",
    var port: Int = 9000,
    var user: String = "",
    var password: String = "",
    var bucket: String = "files"
)

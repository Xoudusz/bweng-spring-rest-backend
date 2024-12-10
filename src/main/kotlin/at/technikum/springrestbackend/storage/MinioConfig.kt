package at.technikum.springrestbackend.storage

import at.technikum.springrestbackend.property.MinioProperties
import io.minio.MinioClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConfig(
    private val minioProperties: MinioProperties
) {
    @Bean
    fun minioClient(): MinioClient {
        return MinioClient.builder()
            .endpoint(minioProperties.url, minioProperties.port, false)
            .credentials(minioProperties.user, minioProperties.password)
            .build()
    }
}

package at.technikum.springrestbackend.storage

import at.technikum.springrestbackend.property.MinioProperties
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConfig(
    private val minioProperties: MinioProperties
) {
    @Bean
    fun minioClient(): MinioClient = MinioClient.builder()
        .credentials(minioProperties.user, minioProperties.password)
        .endpoint(
            minioProperties.url,
            minioProperties.port,
            minioProperties.url.contains("https")
        )
        .build()

    @PostConstruct
    fun ensureBucketExists() {
        val bucketExists = minioClient().bucketExists(BucketExistsArgs.builder().bucket(minioProperties.bucket).build())
        if (!bucketExists) {
            minioClient().makeBucket(MakeBucketArgs.builder().bucket(minioProperties.bucket).build())
        }
    }

}
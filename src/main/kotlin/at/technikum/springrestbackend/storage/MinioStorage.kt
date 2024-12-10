package at.technikum.springrestbackend.storage

import at.technikum.springrestbackend.exception.FileException
import at.technikum.springrestbackend.property.MinioProperties
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.util.UUID

@Service
class MinioStorage(
    private val minioClient: MinioClient,
    private val minioProperties: MinioProperties
) : FileStorage {

    override fun upload(file: MultipartFile): String {
        val uuid = UUID.randomUUID().toString()
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioProperties.bucket)
                    .`object`(uuid)
                    .stream(file.inputStream, file.size, -1)
                    .contentType(file.contentType)
                    .build()
            )
            return uuid
        } catch (e: Exception) {
            throw FileException("Upload failed for file with uuid=$uuid", e)
        }
    }

    override fun download(id: String): InputStream {
        return try {
            minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(minioProperties.bucket)
                    .`object`(id)
                    .build()
            )
        } catch (e: Exception) {
            throw FileException("Download failed for id=$id", e)
        }
    }
}

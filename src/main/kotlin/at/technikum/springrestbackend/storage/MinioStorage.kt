package at.technikum.springrestbackend.storage

import at.technikum.springrestbackend.exception.FileException
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.util.UUID

@Service
class MinioStorage(
    private val minioClient: MinioClient,
    @Value("\${minio.bucket}") private val bucketName: String // Inject bucket name
) : FileStorage {

    override fun upload(file: MultipartFile): String {
        val uuid = UUID.randomUUID().toString()
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName) // Use the injected bucket name
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
                    .bucket(bucketName) // Use the injected bucket name
                    .`object`(id)
                    .build()
            )
        } catch (e: Exception) {
            throw FileException("Download failed for id=$id", e)
        }
    }

    override fun delete(id: String) {
        try {
            minioClient.removeObject(
                io.minio.RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(id)
                    .build()
            )
        } catch (e: Exception) {
            throw FileException("Failed to delete file with id=$id", e)
        }
    }
}


package at.technikum.springrestbackend.service

import org.springframework.core.io.InputStreamResource
import org.springframework.web.multipart.MultipartFile

interface FileService {
    fun uploadFile(file: MultipartFile): String
    fun downloadFile(uuid: String): FileDownloadResponse
    fun deleteFile(uuid: String): Boolean
}

data class FileDownloadResponse(
    val fileName: String,
    val contentType: String,
    val resource: InputStreamResource
)
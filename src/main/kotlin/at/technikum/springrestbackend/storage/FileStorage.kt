package at.technikum.springrestbackend.storage

import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

interface FileStorage {
    fun upload(file: MultipartFile): String
    fun download(id: String): InputStream
    fun delete(id: String)
}
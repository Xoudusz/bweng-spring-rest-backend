package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.entity.File
import at.technikum.springrestbackend.exception.FileException
import at.technikum.springrestbackend.repository.FileRepository
import at.technikum.springrestbackend.storage.FileStorage
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class FileServiceImpl(
    private val fileStorage: FileStorage,
    private val fileRepository: FileRepository
) : FileService {

    private val allowedContentTypes = listOf("image/jpeg", "image/png")

    @Transactional
    override fun uploadFile(file: MultipartFile, uploader: String): String {
        // Validate content type
        if (file.contentType !in allowedContentTypes) {
            throw IllegalArgumentException("Invalid file type. Only images are allowed.")
        }

        val uuid = fileStorage.upload(file)
        val entity = File(
            uuid = uuid,
            fileName = file.originalFilename ?: uuid,
            contentType = file.contentType ?: "application/octet-stream",
            uploader = uploader
        )
        fileRepository.save(entity)
        return uuid
    }

    @Transactional(readOnly = true)
    override fun downloadFile(uuid: String): FileDownloadResponse {
        val fileEntity: File = fileRepository.findByUuid(uuid)
            ?: throw FileException("File with uuid=$uuid not found")

        val inputStream = fileStorage.download(uuid)
        val resource = InputStreamResource(inputStream)

        return FileDownloadResponse(
            fileName = fileEntity.fileName,
            contentType = fileEntity.contentType,
            resource = resource
        )
    }

    @Transactional
    override fun deleteFile(uuid: String): Boolean {
        val fileEntity = fileRepository.findByUuid(uuid)
            ?: throw FileException("File with uuid=$uuid not found")

        fileStorage.delete(uuid)
        fileRepository.delete(fileEntity)

        return true
    }
}

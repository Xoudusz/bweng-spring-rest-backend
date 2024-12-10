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


    @Transactional
    override fun uploadFile(file: MultipartFile): String {
        val uuid = fileStorage.upload(file)
        val entity = File(
            uuid = uuid,
            fileName = file.originalFilename ?: uuid,
            contentType = file.contentType ?: "application/octet-stream"
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

}

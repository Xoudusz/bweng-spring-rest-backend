package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.service.FileService
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

data class FileUploadResponse(val uuid: String, val message: String)

@RestController
@RequestMapping("/api/files")
class FileController(
    private val fileService: FileService
) {
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadFile(@RequestPart("file") file: MultipartFile): FileUploadResponse {
        // Retrieve the authenticated user's username from the security context
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name // This should correspond to "sub" in JWT payload

        // Pass the username to the service so it can be saved as the uploader
        val uuid = fileService.uploadFile(file, username)
        return FileUploadResponse(uuid = uuid, message = "File uploaded successfully.")
    }


    @GetMapping("/{uuid}")
    fun downloadFile(@PathVariable uuid: String): ResponseEntity<InputStreamResource> {
        val fileDownloadResponse = fileService.downloadFile(uuid)

        val headers = org.springframework.http.HttpHeaders().apply {
            add("Content-Disposition", "attachment; filename=\"${fileDownloadResponse.fileName}\"")
        }

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(MediaType.parseMediaType(fileDownloadResponse.contentType))
            .body(fileDownloadResponse.resource)
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFile(@PathVariable uuid: String) {
        fileService.deleteFile(uuid)
    }
}

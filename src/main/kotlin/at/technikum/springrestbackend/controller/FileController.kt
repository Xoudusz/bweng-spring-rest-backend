package at.technikum.springrestbackend.controller



import at.technikum.springrestbackend.service.FileService
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile


data class FileUploadResponse(val uuid: String, val message: String)

@RestController
@RequestMapping("/files")
class FileController(
    private val fileService: FileService
) {
    /**
     * Endpoint to upload a file.
     * Expects multipart/form-data.
     * Returns reference (uuid) of the uploaded file.
     */
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    fun uploadFile(@RequestPart("file") file: MultipartFile): FileUploadResponse {
        val uuid = fileService.uploadFile(file)
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




}

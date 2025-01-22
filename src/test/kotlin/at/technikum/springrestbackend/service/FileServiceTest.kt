package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.entity.File
import at.technikum.springrestbackend.exception.FileException
import at.technikum.springrestbackend.repository.FileRepository
import at.technikum.springrestbackend.storage.FileStorage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.util.*

class FileServiceTest {

    private val fileStorage: FileStorage = mock(FileStorage::class.java)
    private val fileRepository: FileRepository = mock(FileRepository::class.java)
    private val allowedContentTypes = listOf("image/png", "image/jpeg")
    private val fileService = FileServiceImpl(fileStorage, fileRepository, allowedContentTypes)

    @Test
    fun `should upload file successfully`() {
        val mockFile = mock(MultipartFile::class.java)
        val uploader = "testUser"
        val uuid = UUID.randomUUID().toString()

        `when`(mockFile.contentType).thenReturn("image/png")
        `when`(mockFile.originalFilename).thenReturn("test.png")
        `when`(fileStorage.upload(mockFile)).thenReturn(uuid)

        val result = fileService.uploadFile(mockFile, uploader)

        assertEquals(uuid, result)
        verify(fileRepository, times(1)).save(any(File::class.java))
        verify(fileStorage, times(1)).upload(mockFile)
    }

    @Test
    fun `should throw exception for invalid content type`() {
        val mockFile = mock(MultipartFile::class.java)
        `when`(mockFile.contentType).thenReturn("text/plain")

        val uploader = "testUser"

        assertThrows<IllegalArgumentException> {
            fileService.uploadFile(mockFile, uploader)
        }
    }

    @Test
    fun `should download file successfully`() {
        val uuid = UUID.randomUUID().toString()
        val mockFileEntity = File(uuid, "test.png", "image/png", uploader = "testUser")
        val inputStream = ByteArrayInputStream(ByteArray(0))

        `when`(fileRepository.findByUuid(uuid)).thenReturn(mockFileEntity)
        `when`(fileStorage.download(uuid)).thenReturn(inputStream)

        val result = fileService.downloadFile(uuid)

        assertEquals("test.png", result.fileName)
        assertEquals("image/png", result.contentType)
        assertNotNull(result.resource)
    }

    @Test
    fun `should throw exception for file not found`() {
        val uuid = UUID.randomUUID().toString()

        `when`(fileRepository.findByUuid(uuid)).thenReturn(null)

        assertThrows<FileException> {
            fileService.downloadFile(uuid)
        }
    }

    @Test
    fun `should delete file successfully`() {
        val uuid = UUID.randomUUID().toString()
        val mockFileEntity = File(uuid, "test.png", "image/png", uploader = "testUser")

        `when`(fileRepository.findByUuid(uuid)).thenReturn(mockFileEntity)

        val result = fileService.deleteFile(uuid)

        assertTrue(result)
        verify(fileRepository, times(1)).delete(mockFileEntity)
        verify(fileStorage, times(1)).delete(uuid)
    }

    @Test
    fun `should throw exception when deleting non-existent file`() {
        val uuid = UUID.randomUUID().toString()

        `when`(fileRepository.findByUuid(uuid)).thenReturn(null)

        assertThrows<FileException> {
            fileService.deleteFile(uuid)
        }
    }
}

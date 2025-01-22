package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.service.FileService
import at.technikum.springrestbackend.service.FileDownloadResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.InputStreamResource
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.util.*

@WebMvcTest(FileController::class)
class FileControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var fileService: FileService

    @BeforeEach
    fun setUp() {
        // Set up a mock security context
        val authentication = mock(Authentication::class.java)
        `when`(authentication.name).thenReturn("testUser")
        val securityContext = mock(SecurityContext::class.java)
        `when`(securityContext.authentication).thenReturn(authentication)
        SecurityContextHolder.setContext(securityContext)
    }

    @Test
    fun `should upload file successfully`() {
        // Arrange
        val mockFile = MockMultipartFile(
            "file",
            "test.png",
            "image/png",
            "Test file content".toByteArray()
        )
        val uuid = UUID.randomUUID().toString()

        `when`(fileService.uploadFile(any(MultipartFile::class.java), anyString())).thenReturn(uuid)

        // Act & Assert
        mockMvc.perform(
            multipart("/api/files")
                .file(mockFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.uuid").value(uuid))
            .andExpect(jsonPath("$.message").value("File uploaded successfully."))

        verify(fileService, times(1)).uploadFile(any(MultipartFile::class.java), eq("testUser"))
    }

    @Test
    fun `should download file successfully`() {
        // Arrange
        val uuid = UUID.randomUUID().toString()
        val fileName = "test.png"
        val contentType = "image/png"
        val resource = ByteArrayInputStream("Test content".toByteArray())

        `when`(fileService.downloadFile(uuid)).thenReturn(
            FileDownloadResponse(fileName, contentType, InputStreamResource(resource))
        )

        // Act & Assert
        mockMvc.perform(get("/api/files/$uuid"))
            .andExpect(status().isOk)
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"$fileName\""))
            .andExpect(content().contentType(contentType))

        verify(fileService, times(1)).downloadFile(uuid)
    }

    @Test
    fun `should delete file successfully`() {
        // Arrange
        val uuid = UUID.randomUUID().toString()

        doNothing().`when`(fileService).deleteFile(uuid)

        // Act & Assert
        mockMvc.perform(delete("/api/files/$uuid"))
            .andExpect(status().isNoContent)

        verify(fileService, times(1)).deleteFile(uuid)
    }
}

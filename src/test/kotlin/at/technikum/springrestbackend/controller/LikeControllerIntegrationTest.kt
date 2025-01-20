package at.technikum.springrestbackend.controller

import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import at.technikum.springrestbackend.dto.LikeCreateDTO
import at.technikum.springrestbackend.entity.Like
import at.technikum.springrestbackend.service.LikeServiceImpl
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import java.util.*

@WebMvcTest(LikeController::class)
class LikeControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var likeService: LikeServiceImpl

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should create a like`() {
        val likeCreateDTO = LikeCreateDTO(UUID.randomUUID(), UUID.randomUUID())
        val like = Like(UUID.randomUUID(), likeCreateDTO.userId, likeCreateDTO.postId)

        `when`(likeService.createLike(likeCreateDTO)).thenReturn(like)

        mockMvc.perform(
            post("/api/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(likeCreateDTO))
        )
            .andExpect(status().isCreated)
    }

    @Test
    fun `should delete a like`() {
        val likeId = UUID.randomUUID()

        doNothing().`when`(likeService).deleteLike(likeId)

        mockMvc.perform(delete("/api/likes/$likeId"))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should get likes by user`() {
        val userId = UUID.randomUUID()
        val like = Like(UUID.randomUUID(), userId, UUID.randomUUID())

        `when`(likeService.getLikesByUser(userId)).thenReturn(listOf(like))

        mockMvc.perform(get("/api/likes/user/$userId"))
            .andExpect(status().isOk)
    }

    @Test
    fun `should get likes by post`() {
        val postId = UUID.randomUUID()
        val like = Like(UUID.randomUUID(), UUID.randomUUID(), postId)

        `when`(likeService.getLikesByPost(postId)).thenReturn(listOf(like))

        mockMvc.perform(get("/api/likes/post/$postId"))
            .andExpect(status().isOk)
    }
}

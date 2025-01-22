package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.FollowDTO
import at.technikum.springrestbackend.service.FollowService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@WebMvcTest(FollowController::class)
class FollowControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var followService: FollowService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should follow a user`() {
        val followDTO = FollowDTO(UUID.randomUUID(), UUID.randomUUID())

        `when`(followService.followUser(followDTO.followerId, followDTO.followingId)).thenReturn(mock())

        mockMvc.perform(
            post("/api/follows")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDTO))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should unfollow a user`() {
        val followDTO = FollowDTO(UUID.randomUUID(), UUID.randomUUID())

        doNothing().`when`(followService).unfollowUser(followDTO.followerId, followDTO.followingId)

        mockMvc.perform(
            delete("/api/follows")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followDTO))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `should get followers`() {
        val userId = UUID.randomUUID()
        val followDTO = FollowDTO(UUID.randomUUID(), userId)

        `when`(followService.getFollowers(userId)).thenReturn(listOf(followDTO))

        mockMvc.perform(get("/api/follows/followers/$userId"))
            .andExpect(status().isOk)
    }
    @Test
    fun `should get following`(){
        val userId = UUID.randomUUID()
        val followDTO = FollowDTO(UUID.randomUUID(), userId)

        `when`(followService.getFollowing(userId)).thenReturn(listOf(followDTO))

        mockMvc.perform(get("/api/follows/following/$userId"))
            .andExpect(status().isOk)
    }
}

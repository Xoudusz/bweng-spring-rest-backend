package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.LikeCreateDTO
import at.technikum.springrestbackend.entity.Like
import at.technikum.springrestbackend.exception.notFound.UserNotFoundException
import at.technikum.springrestbackend.repository.LikeRepository
import at.technikum.springrestbackend.repository.PostRepository
import at.technikum.springrestbackend.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

class LikeServiceImplTest {

    private val likeRepository: LikeRepository = mock(LikeRepository::class.java)
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val postRepository: PostRepository = mock(PostRepository::class.java)
    private val likeService = LikeServiceImpl(postRepository, userRepository, likeRepository)

    @Test
    fun `should create a like`() {
        val userId = UUID.randomUUID()
        val postId = UUID.randomUUID()
        val likeCreateDTO = LikeCreateDTO(userId, postId)

        `when`(userRepository.existsById(userId)).thenReturn(true)
        `when`(postRepository.existsById(postId)).thenReturn(true)
        `when`(likeRepository.save(any(Like::class.java))).thenAnswer { it.arguments[0] }

        val result = likeService.createLike(likeCreateDTO)

        assertEquals(userId, result.userId)
        assertEquals(postId, result.postId)
        verify(likeRepository, times(1)).save(any(Like::class.java))
    }

    @Test
    fun `should throw UserNotFoundException when user does not exist`() {
        val userId = UUID.randomUUID()
        val postId = UUID.randomUUID()
        val likeCreateDTO = LikeCreateDTO(userId, postId)

        `when`(userRepository.existsById(userId)).thenReturn(false)

        assertThrows<UserNotFoundException> {
            likeService.createLike(likeCreateDTO)
        }
    }

    @Test
    fun `should delete a like`() {
        val likeId = UUID.randomUUID()

        `when`(likeRepository.existsById(likeId)).thenReturn(true)

        likeService.deleteLike(likeId)

        verify(likeRepository, times(1)).deleteById(likeId)
    }

    @Test
    fun `should get likes by post`() {
        val postId = UUID.randomUUID()
        val like = Like(id = UUID.randomUUID(), userId = UUID.randomUUID(), postId = postId)

        `when`(postRepository.existsById(postId)).thenReturn(true)
        `when`(likeRepository.findByPostId(postId)).thenReturn(listOf(like))

        val result = likeService.getLikesByPost(postId)

        assertEquals(1, result.size)
        assertEquals(postId, result[0].postId)
    }
}

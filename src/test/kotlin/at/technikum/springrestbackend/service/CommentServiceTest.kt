package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.CommentCreateDTO
import at.technikum.springrestbackend.entity.Comment
import at.technikum.springrestbackend.exception.notFound.UserNotFoundException
import at.technikum.springrestbackend.repository.CommentRepository
import at.technikum.springrestbackend.repository.PostRepository
import at.technikum.springrestbackend.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

class CommentServiceTest {

    private val commentRepository: CommentRepository = mock(CommentRepository::class.java)
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val postRepository: PostRepository = mock(PostRepository::class.java)
    private val commentService = CommentServiceImpl(postRepository, userRepository, commentRepository)

    @Test
    fun `should create a comment`() {
        val userId = UUID.randomUUID()
        val postId = UUID.randomUUID()
        val commentCreateDTO = CommentCreateDTO(userId, postId, "This is a comment")

        `when`(userRepository.existsById(userId)).thenReturn(true)
        `when`(postRepository.existsById(postId)).thenReturn(true)
        `when`(commentRepository.save(any(Comment::class.java))).thenAnswer { it.arguments[0] }

        val result = commentService.createComment(commentCreateDTO)

        assertEquals(userId, result.userId)
        assertEquals(postId, result.postId)
        assertEquals("This is a comment", result.content)
        verify(commentRepository, times(1)).save(any(Comment::class.java))
    }

    @Test
    fun `should throw UserNotFoundException when user does not exist`() {
        val userId = UUID.randomUUID()
        val postId = UUID.randomUUID()
        val commentCreateDTO = CommentCreateDTO(userId, postId, "This is a comment")

        `when`(userRepository.existsById(userId)).thenReturn(false)

        assertThrows<UserNotFoundException> {
            commentService.createComment(commentCreateDTO)
        }
    }

    @Test
    fun `should get comments by post`() {
        val postId = UUID.randomUUID()
        val comment = Comment(UUID.randomUUID(), UUID.randomUUID(), postId, "Test comment")

        `when`(postRepository.existsById(postId)).thenReturn(true)
        `when`(commentRepository.findByPostId(postId)).thenReturn(listOf(comment))

        val result = commentService.getCommentsByPost(postId)

        assertEquals(1, result.size, "Expected exactly 1 comment to be returned")
        assertEquals(postId, result[0].postId, "Post ID of the returned comment does not match")
        assertEquals("Test comment", result[0].content, "Content of the returned comment does not match")


        verify(postRepository, times(1)).existsById(postId)
        verify(commentRepository, times(1)).findByPostId(postId)
    }

    @Test
    fun `should delete a comment`() {
        val commentId = UUID.randomUUID()

        `when`(commentRepository.existsById(commentId)).thenReturn(true)

        commentService.deleteComment(commentId)

        verify(commentRepository, times(1)).deleteById(commentId)
    }

    @Test
    fun `should get comments by user`(){

        val userId = UUID.randomUUID()
        val postId = UUID.randomUUID()
        val comment = Comment(UUID.randomUUID(), userId, postId, "This is a comment")

        `when`(userRepository.existsById(userId)).thenReturn(true)
        `when`(commentRepository.findByUserId(userId)).thenReturn(listOf(comment))

        val result = commentService.getCommentsByUser(userId)

        assertEquals(1, result.size)
        assertEquals(userId, result[0].userId)
        assertEquals("This is a comment", result[0].content)

        verify(userRepository, times(1)).existsById(userId)
        verify(commentRepository, times(1)).findByUserId(userId)


    }

    @Test
    fun`should get all comments`(){
        val comment1 = Comment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),"first comment")
        val comment2 = Comment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),"second comment")
        val mockComments = listOf(comment1, comment2)

        `when`(commentRepository.findAll()).thenReturn(mockComments)
        val result = commentRepository.findAll()

        assertEquals(2, result.size)
        assertEquals(comment1.content, result[0].content)
        assertEquals(comment2.content, result[1].content)

        verify(commentRepository, times(1)).findAll()

    }
}

package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.CommentCreateDTO
import at.technikum.springrestbackend.entity.Comment
import at.technikum.springrestbackend.service.CommentServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommentControllerTest {

    @Mock
    private lateinit var commentService: CommentServiceImpl

    @InjectMocks
    private lateinit var commentController: CommentController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should return all comments`() {
        val post1Id = UUID.randomUUID()
        val post2Id = UUID.randomUUID()
        val comment1 = Comment(UUID.randomUUID(), UUID.randomUUID(), post1Id, "first comment")
        val comment2 = Comment(UUID.randomUUID(), UUID.randomUUID(), post2Id, "second comment")
        val comments = listOf(comment1, comment2)

        `when`(commentService.getAllComments()).thenReturn(comments)

        val result = commentController.getAllComments()

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(2, result.body?.size)
        assertEquals("first comment", result.body?.get(0)?.content)
        assertEquals("second comment", result.body?.get(1)?.content)
        verify(commentService, times(1)).getAllComments()
    }

    @Test
    fun `should return empty list when no comments exist`() {
        `when`(commentService.getAllComments()).thenReturn(emptyList())

        val result = commentController.getAllComments()

        assertTrue(result.body?.isEmpty() == true, "Expected the result body to be an empty list")
        assertEquals(HttpStatus.OK, result.statusCode, "Expected HTTP status to be OK")
        verify(commentService, times(1)).getAllComments()
    }

    @Test
    fun `should create a comment`() {
        val commentCreateDTO = CommentCreateDTO(UUID.randomUUID(), UUID.randomUUID(), "This is a comment")
        val comment = Comment(UUID.randomUUID(), commentCreateDTO.userId, commentCreateDTO.postId, commentCreateDTO.content)

        `when`(commentService.createComment(commentCreateDTO)).thenReturn(comment)

        val result = commentController.createComment(commentCreateDTO)

        assertEquals("This is a comment", result.body?.content)
        assertEquals(commentCreateDTO.userId, result.body?.userId)
        assertEquals(commentCreateDTO.postId, result.body?.postId)
        assertEquals(HttpStatus.CREATED, result.statusCode)
        verify(commentService, times(1)).createComment(commentCreateDTO)
    }

    @Test
    fun `should delete a comment`() {
        val commentId = UUID.randomUUID()

        doNothing().`when`(commentService).deleteComment(commentId)

        commentController.deleteComment(commentId)

        verify(commentService, times(1)).deleteComment(commentId)
    }

    @Test
    fun `should get comments by post`() {
        val postId = UUID.randomUUID()
        val comment = Comment(UUID.randomUUID(), UUID.randomUUID(), postId, "Test comment")

        `when`(commentService.getCommentsByPost(postId)).thenReturn(listOf(comment))

        val result = commentController.getCommentsByPost(postId)

        // Extract the ResponseEntity body
        assertEquals(1, result.body?.size, "Expected exactly 1 comment")
        assertEquals("Test comment", result.body?.get(0)?.content, "Expected comment content to match")
        assertEquals(postId, result.body?.get(0)?.postId, "Expected postId to match")
        assertEquals(HttpStatus.OK, result.statusCode, "Expected HTTP status to be OK")

        verify(commentService, times(1)).getCommentsByPost(postId)
    }


    @Test
    fun `should get comments by user`() {
        val userId = UUID.randomUUID()
        val comment = Comment(UUID.randomUUID(), userId, UUID.randomUUID(), "Test comment")

        `when`(commentService.getCommentsByUser(userId)).thenReturn(listOf(comment))

        val result = commentController.getCommentsByUser(userId)

        // Extract the ResponseEntity body
        assertEquals(1, result.body?.size, "Expected exactly 1 comment")
        assertEquals("Test comment", result.body?.get(0)?.content, "Expected comment content to match")
        assertEquals(userId, result.body?.get(0)?.userId, "Expected userId to match")
        assertEquals(HttpStatus.OK, result.statusCode, "Expected HTTP status to be OK")

        verify(commentService, times(1)).getCommentsByUser(userId)
    }

}

package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.controller.PostController
import at.technikum.springrestbackend.dto.PostCreateDTO
import at.technikum.springrestbackend.dto.PostUpdateDTO
import at.technikum.springrestbackend.entity.Post
import at.technikum.springrestbackend.service.PostServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class PostControllerTest {

    @Mock
    private lateinit var postServiceImpl: PostServiceImpl

    @InjectMocks
    private lateinit var postController: PostController

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    //getPostByIdTests
    @Test
    fun `should retrieve post by ID and return OK status`() {
        // Arrange
        val postId = UUID.randomUUID()
        val post = Post(postId, UUID.randomUUID(), "Content", LocalDateTime.now())
        `when`(postServiceImpl.getPostById(postId)).thenReturn(post)

        // Act
        val response = postController.getPostById(postId)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(post, response.body)
        verify(postServiceImpl, times(1)).getPostById(postId)
    }

    //createPostTests
    @Test
    fun `should create post and return CREATED status`() {
        // Arrange
        val postCreateDTO = PostCreateDTO("Content", UUID.randomUUID())
        val createdPost = Post(UUID.randomUUID(), UUID.randomUUID(), "Content", LocalDateTime.now())
        `when`(postServiceImpl.createPost(postCreateDTO)).thenReturn(createdPost)

        // Act
        val response = postController.createPost(postCreateDTO)

        // Assert
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(createdPost, response.body)
        verify(postServiceImpl, times(1)).createPost(postCreateDTO)
    }

    //getAllPostsTests
    @Test
    fun `should retrieve all posts and return OK status`() {
        // Arrange
        val posts = listOf(
            Post(UUID.randomUUID(), UUID.randomUUID(), "Content1", LocalDateTime.now()),
            Post(UUID.randomUUID(), UUID.randomUUID(), "Content2", LocalDateTime.now())
        )
        `when`(postServiceImpl.getAllPosts()).thenReturn(posts)

        // Act
        val response = postController.getAllPosts()

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(posts, response.body)
        verify(postServiceImpl, times(1)).getAllPosts()
    }

    //getPostsByUserTests
    @Test
    fun `should retrieve posts by user ID and return OK status`() {
        // Arrange
        val userId = UUID.randomUUID()
        val posts = listOf(
            Post(UUID.randomUUID(), UUID.randomUUID(), "Content1", LocalDateTime.now()),
            Post(UUID.randomUUID(), UUID.randomUUID(), "Content2", LocalDateTime.now())
        )
        `when`(postServiceImpl.getPostsByUser(userId)).thenReturn(posts)

        // Act
        val response = postController.getPostsByUser(userId)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(posts, response.body)
        verify(postServiceImpl, times(1)).getPostsByUser(userId)
    }

    //updatePostTests
    @Test
    fun `should update post and return OK status`() {
        // Arrange
        val postId = UUID.randomUUID()
        val postUpdateDTO = PostUpdateDTO("Content")
        val updatedPost = Post(postId, UUID.randomUUID(), "Content2", LocalDateTime.now())
        `when`(postServiceImpl.updatePost(postId, postUpdateDTO)).thenReturn(updatedPost)

        // Act
        val response = postController.updatePost(postId, postUpdateDTO)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updatedPost, response.body)
        verify(postServiceImpl, times(1)).updatePost(postId, postUpdateDTO)
    }

    //deletePostTests
    @Test
    fun `should delete post and return NO_CONTENT status`() {
        // Arrange
        val postId = UUID.randomUUID()

        // Act
        val response = postController.deletePost(postId)

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify(postServiceImpl, times(1)).deletePost(postId)
    }

}

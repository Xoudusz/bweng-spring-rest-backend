package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.PostCreateDTO
import at.technikum.springrestbackend.dto.PostResponseDTO
import at.technikum.springrestbackend.dto.PostUpdateDTO
import at.technikum.springrestbackend.service.PostServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
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
        postServiceImpl = mock(PostServiceImpl::class.java)
        postController = PostController(postServiceImpl)

        // Mock SecurityContext
        val authentication = mock(Authentication::class.java)
        `when`(authentication.name).thenReturn("user") // Set the authenticated user's name
        val securityContext = mock(SecurityContext::class.java)
        `when`(securityContext.authentication).thenReturn(authentication)
        SecurityContextHolder.setContext(securityContext)
    }

    //getPostByIdTests
    @Test
    fun `should retrieve post by ID and return OK status`() {
        // Arrange
        val postId = UUID.randomUUID()
        val postResponseDTO = PostResponseDTO(postId, "Content", "user", LocalDateTime.now(), null, null)
        `when`(postServiceImpl.getPostById(postId)).thenReturn(postResponseDTO)

        // Act
        val response = postController.getPostById(postId)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(postResponseDTO, response.body)
        verify(postServiceImpl, times(1)).getPostById(postId)
    }

    //createPostTests
    @Test
    fun `should create post and return CREATED status`() {
        // Arrange
        val postCreateDTO = PostCreateDTO("Content", null)
        val postResponseDTO = PostResponseDTO(
            id = UUID.randomUUID(),
            content = "Content",
            username = "user",
            createdAt = LocalDateTime.now(),
            file = null,
            profilePicture = null
        )
        `when`(postServiceImpl.createPost(postCreateDTO, "user")).thenReturn(postResponseDTO)

        // Act
        val response = postController.createPost(postCreateDTO)

        // Assert
        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(postResponseDTO, response.body)
        verify(postServiceImpl, times(1)).createPost(postCreateDTO, "user")
    }


    //getAllPostsTests
    @Test
    fun `should retrieve all posts and return OK status`() {
        val responseDTOs = listOf(
            PostResponseDTO(UUID.randomUUID(), "Content1", "user1", LocalDateTime.now(), null, null),
            PostResponseDTO(UUID.randomUUID(), "Content2", "user2", LocalDateTime.now(), null, null)
        )
        `when`(postServiceImpl.getAllPosts()).thenReturn(responseDTOs)

        val response = postController.getAllPosts()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(responseDTOs, response.body)
        verify(postServiceImpl, times(1)).getAllPosts()
    }
    //getPostsByUserTests
    @Test
    fun `should retrieve posts by user ID and return OK status`() {
        // Arrange
        val userId = UUID.randomUUID()
        val posts = listOf(
            PostResponseDTO(UUID.randomUUID(), "Content1", "user1", LocalDateTime.now(),null ,null),
            PostResponseDTO(UUID.randomUUID(), "Content2", "user2", LocalDateTime.now(),null, null)
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
        val postId = UUID.randomUUID()
        val postUpdateDTO = PostUpdateDTO("Updated Content")
        val postResponseDTO = PostResponseDTO(postId, "Updated Content", "user", LocalDateTime.now(), null, null
        )
        `when`(postServiceImpl.updatePost(postId, postUpdateDTO)).thenReturn(postResponseDTO)

        val response = postController.updatePost(postId, postUpdateDTO)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(postResponseDTO, response.body)
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


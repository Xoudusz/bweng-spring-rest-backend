package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.PostCreateDTO
import at.technikum.springrestbackend.dto.PostUpdateDTO

import at.technikum.springrestbackend.entity.File
import at.technikum.springrestbackend.entity.Post
import at.technikum.springrestbackend.entity.User
import at.technikum.springrestbackend.entity.enums.Role
import at.technikum.springrestbackend.exception.notFound.PostNotFoundException
import at.technikum.springrestbackend.exception.notFound.UserNotFoundException
import at.technikum.springrestbackend.repository.FileRepository
import at.technikum.springrestbackend.repository.PostRepository
import at.technikum.springrestbackend.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.mockito.kotlin.anyOrNull
import java.io.FileNotFoundException
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

class PostServiceTest {
    private val postRepository: PostRepository = mock(PostRepository::class.java)
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val fileRepository: FileRepository = mock(FileRepository::class.java)
    private val postService = PostServiceImpl(postRepository, userRepository, fileRepository)

    @Test
    fun `should create post successfully`() {
        val userId = UUID.randomUUID()
        val user = User(userId, "testUser", "example@mail.com","Password1*", Role.USER,"Mr.", "Austria" )
        val fileId = UUID.randomUUID().toString()
        val file = File(fileId, "testFile.png", "image/png", Instant.now() ,"user")
        val dto = PostCreateDTO(content = "Test content", fileId = fileId)

        `when`(userRepository.findByUsername("testUser")).thenReturn(user)
        `when`(fileRepository.findById(fileId)).thenReturn(Optional.of(file))
        `when`(postRepository.save(any(Post::class.java))).thenAnswer { it.arguments[0] }

        val result = postService.createPost(dto, "testUser")

        assertEquals("Test content", result.content)
        assertEquals("testUser", result.username)
        assertEquals(file, result.file)
        verify(postRepository, times(1)).save(any(Post::class.java))
    }

    @Test
    fun `should throw UserNotFoundException when creating post with invalid user`() {
        val dto = PostCreateDTO(content = "Test content")

        `when`(userRepository.findByUsername("nonexistentUser")).thenReturn(null)

        assertThrows<UserNotFoundException> {
            postService.createPost(dto, "nonexistentUser")
        }
    }

    @Test
    fun `should throw FileNotFoundException when creating post with invalid file`() {
        val userId = UUID.randomUUID()
        val user = User(userId, "testUser", "example@mail.com","Password1*", Role.USER,"Mr.", "Austria" )

        val fileId = UUID.randomUUID().toString()
        val dto = PostCreateDTO(content = "Test content", fileId = fileId)

        `when`(userRepository.findByUsername("testUser")).thenReturn(user)
        `when`(fileRepository.findById(fileId)).thenReturn(Optional.empty())

        assertThrows<FileNotFoundException> {
            postService.createPost(dto, "testUser")
        }
    }

    @Test
    fun `should retrieve post by ID`() {
        val postId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val user = User(userId, "testUser", "example@mail.com","Password1*", Role.USER,"Mr.", "Austria" )

        val post = Post(postId, user, content = "Test content")

        `when`(postRepository.findById(postId)).thenReturn(Optional.of(post))

        val result = postService.getPostById(postId)

        assertEquals("Test content", result.content)
        assertEquals("testUser", result.username)
    }

    @Test
    fun `should retrieve posts by username and map to PostResponseDTO`() {
        val username = "testUser"
        val userId = UUID.randomUUID()
        val user = User(userId, username, "example@mail.com","Password1*", Role.USER,"Mr.", "Austria" )

        val posts = listOf(
            Post(id = UUID.randomUUID(), user = user, content = "Content1", file = null, createdAt = LocalDateTime.now()),
            Post(id = UUID.randomUUID(), user = user, content = "Content2", file = null, createdAt = LocalDateTime.now())
        )

        `when`(postRepository.findByUserUsername(username)).thenReturn(posts)

        // Act
        val response = postService.getPostByUsername(username)

        // Assert
        assertEquals(2, response.size)
        assertEquals("Content1", response[0].content)
        assertEquals("Content2", response[1].content)
        assertEquals(username, response[0].username)
        assertEquals(username, response[1].username)
        verify(postRepository, times(1)).findByUserUsername(username)
    }

    @Test
    fun `should retrieve all posts and map to PostResponseDTO`() {
        // Arrange
        val user1 = User(id = UUID.randomUUID(), "user1", "example@1.com","Password1*", Role.USER,"Mr.", "Austria")
        val user2 = User(id = UUID.randomUUID(), "user2", "example@2.com","Password2*", Role.USER,"Dr.", "Austria")
        val posts = listOf(
            Post(id = UUID.randomUUID(), user = user1, content = "Content1", file = null, createdAt = LocalDateTime.now()),
            Post(id = UUID.randomUUID(), user = user2, content = "Content2", file = null, createdAt = LocalDateTime.now())
        )

        `when`(postRepository.findAll()).thenReturn(posts)

        // Act
        val response = postService.getAllPosts()

        // Assert
        assertEquals(2, response.size)
        assertEquals("Content1", response[0].content)
        assertEquals("Content2", response[1].content)
        assertEquals("user1", response[0].username)
        assertEquals("user2", response[1].username)
        verify(postRepository, times(1)).findAll()
    }

    @Test
    fun `should throw PostNotFoundException when retrieving non-existent post`() {
        val postId = UUID.randomUUID()

        `when`(postRepository.findById(postId)).thenReturn(Optional.empty())

        assertThrows<PostNotFoundException> {
            postService.getPostById(postId)
        }
    }

    @Test
    fun `should delete post successfully`() {
        val postId = UUID.randomUUID()

        `when`(postRepository.existsById(postId)).thenReturn(true)

        postService.deletePost(postId)

        verify(postRepository, times(1)).deleteById(postId)
    }

    @Test
    fun `should throw PostNotFoundException when deleting non-existent post`() {
        val postId = UUID.randomUUID()

        `when`(postRepository.existsById(postId)).thenReturn(false)

        assertThrows<PostNotFoundException> {
            postService.deletePost(postId)
        }
    }
    @Test
    fun `should update post and return PostResponseDTO`() {
        // Arrange
        val postId = UUID.randomUUID()
        val userId = UUID.randomUUID()
        val user = User(userId, "testUser", "example@mail.com","Password1*", Role.USER,"Mr.", "Austria" )

        val existingPost = Post(id = postId, user = user, content = "Old Content", file = null, createdAt = LocalDateTime.now())
        val postUpdateDTO = PostUpdateDTO(content = "Updated Content")
        val updatedPost = existingPost.copy(content = "Updated Content")

        `when`(postRepository.findById(postId)).thenReturn(Optional.of(existingPost))
        `when`(postRepository.save(updatedPost)).thenReturn(updatedPost)

        // Act
        val response = postService.updatePost(postId, postUpdateDTO)

        // Assert
        assertEquals(postId, response.id)
        assertEquals("Updated Content", response.content)
        assertEquals("testUser", response.username)
        verify(postRepository, times(1)).findById(postId)
        verify(postRepository, times(1)).save(updatedPost)
    }

    @Test
    fun `should throw PostNotFoundException when post ID does not exist`() {
        // Arrange
        val postId = UUID.randomUUID()
        val postUpdateDTO = PostUpdateDTO(content = "Updated Content")

        `when`(postRepository.findById(postId)).thenReturn(Optional.empty())

        // Act & Assert
        assertThrows<PostNotFoundException> {
            postService.updatePost(postId, postUpdateDTO)
        }
        verify(postRepository, times(1)).findById(postId)
        verify(postRepository, never()).save(any())
    }

    @Test
    fun `should retrieve posts by user ID and return PostResponseDTO list`() {
        // Arrange
        val userId = UUID.randomUUID()
        val user = User(userId, "testUser", "example@mail.com","Password1*", Role.USER,"Mr.", "Austria" )

        val posts = listOf(
            Post(id = UUID.randomUUID(), user = user, content = "Content1", file = null, createdAt = LocalDateTime.now()),
            Post(id = UUID.randomUUID(), user = user, content = "Content2", file = null, createdAt = LocalDateTime.now())
        )

        `when`(userRepository.existsById(userId)).thenReturn(true)
        `when`(postRepository.findByUserId(userId)).thenReturn(posts)

        // Act
        val response = postService.getPostsByUser(userId)

        // Assert
        assertEquals(2, response.size)
        assertEquals("Content1", response[0].content)
        assertEquals("Content2", response[1].content)
        assertEquals("testUser", response[0].username)
        verify(userRepository, times(1)).existsById(userId)
        verify(postRepository, times(1)).findByUserId(userId)
    }

    @Test
    fun `should throw UserNotFoundException when user ID does not exist`() {
        // Arrange
        val userId = UUID.randomUUID()

        `when`(userRepository.existsById(userId)).thenReturn(false)

        // Act & Assert
        assertThrows<UserNotFoundException> {
            postService.getPostsByUser(userId)
        }
        verify(userRepository, times(1)).existsById(userId)
        verify(postRepository, never()).findByUserId(anyOrNull())
    }
}


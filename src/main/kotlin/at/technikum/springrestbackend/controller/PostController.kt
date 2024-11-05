package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.PostCreateDTO
import at.technikum.springrestbackend.dto.PostUpdateDTO
import at.technikum.springrestbackend.entity.Post
import at.technikum.springrestbackend.repository.PostRepository
import at.technikum.springrestbackend.repository.UserRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
) {

    @GetMapping("/{id}")
    fun getPostById(@PathVariable id: UUID): ResponseEntity<Post> =
        postRepository.findById(id)
            .map { ResponseEntity(it, HttpStatus.OK) }
            .orElse(ResponseEntity(HttpStatus.NOT_FOUND))

    @PostMapping
    fun createPost(@RequestBody @Valid postCreateDTO: PostCreateDTO): ResponseEntity<Post> {
        return if (userRepository.existsById(postCreateDTO.userId)) {
            val post = Post(userId = postCreateDTO.userId, content = postCreateDTO.content)
            val savedPost = postRepository.save(post)
            ResponseEntity(savedPost, HttpStatus.CREATED)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping
    fun getAllPosts(): ResponseEntity<List<Post>> =
        ResponseEntity(postRepository.findAll(), HttpStatus.OK)

    @GetMapping("/user/{userId}")
    fun getPostsByUser(@PathVariable userId: UUID): ResponseEntity<List<Post>> {
        val posts = postRepository.findByUserId(userId)
        return if (posts.isNotEmpty()) {
            ResponseEntity(posts, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/{id}")
    fun updatePost(@PathVariable id: UUID, @RequestBody @Valid postUpdateDTO: PostUpdateDTO): ResponseEntity<Post> {
        return postRepository.findById(id).map { existingPost ->
            val updatedPost = existingPost.copy(content = postUpdateDTO.content)
            ResponseEntity(postRepository.save(updatedPost), HttpStatus.OK)
        }.orElse(ResponseEntity(HttpStatus.NOT_FOUND))
    }

    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: UUID): ResponseEntity<Void> {
        return if (postRepository.existsById(id)) {
            postRepository.deleteById(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }
}

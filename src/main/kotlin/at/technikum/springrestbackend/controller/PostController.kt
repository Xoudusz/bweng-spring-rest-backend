package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.PostCreateDTO
import at.technikum.springrestbackend.dto.PostResponseDTO
import at.technikum.springrestbackend.dto.PostUpdateDTO
import at.technikum.springrestbackend.service.PostServiceImpl
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postServiceImpl: PostServiceImpl
) {

    @GetMapping("/{id}")
    fun getPostById(@PathVariable id: UUID): ResponseEntity<PostResponseDTO> {
        val post = postServiceImpl.getPostById(id)
        return ResponseEntity(post, HttpStatus.OK)
    }

    @PostMapping
    fun createPost(@RequestBody @Valid postCreateDTO: PostCreateDTO): ResponseEntity<PostResponseDTO> {
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name
        return ResponseEntity(postServiceImpl.createPost(postCreateDTO, username), HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllPosts(): ResponseEntity<List<PostResponseDTO>> {
        val posts = postServiceImpl.getAllPosts()
        return ResponseEntity(posts, HttpStatus.OK)
    }

    @GetMapping("/user/{userId}")
    fun getPostsByUser(@PathVariable userId: UUID): ResponseEntity<List<PostResponseDTO>> {
        val posts = postServiceImpl.getPostsByUser(userId)
        return ResponseEntity(posts, HttpStatus.OK)
    }

    @GetMapping("/user/username/{username}")
    fun getPostByUserUsername(@PathVariable username: String): ResponseEntity<List<PostResponseDTO>> {
        val posts = postServiceImpl.getPostByUsername(username)
        return ResponseEntity(posts, HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun updatePost(@PathVariable id: UUID, @RequestBody @Valid postUpdateDTO: PostUpdateDTO): ResponseEntity<PostResponseDTO> {
        val updatedPost = postServiceImpl.updatePost(id, postUpdateDTO)
        return ResponseEntity(updatedPost, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: UUID): ResponseEntity<Void> {
        postServiceImpl.deletePost(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
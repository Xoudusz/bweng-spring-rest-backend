package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.PostCreateDTO
import at.technikum.springrestbackend.dto.PostUpdateDTO
import at.technikum.springrestbackend.entity.Post
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
    fun getPostById(@PathVariable id: UUID): ResponseEntity<Post> {
        val post = postServiceImpl.getPostById(id)
        return ResponseEntity(post, HttpStatus.OK)
    }

    @PostMapping
    fun createPost(@RequestBody @Valid postCreateDTO: PostCreateDTO): ResponseEntity<Post> {
        // Retrieve the authenticated user's username from the security context
        val authentication = SecurityContextHolder.getContext().authentication
        val username = authentication.name // This should correspond to "sub" in JWT payload

        // Pass the username to the service so it can be saved as the uploader


        return ResponseEntity(postServiceImpl.createPost(postCreateDTO, username), HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllPosts(): ResponseEntity<List<Post>> {
        val posts = postServiceImpl.getAllPosts()
        return ResponseEntity(posts, HttpStatus.OK)
    }

    @GetMapping("/user/{userId}")
    fun getPostsByUser(@PathVariable userId: UUID): ResponseEntity<List<Post>> {
        val posts = postServiceImpl.getPostsByUser(userId)
        return ResponseEntity(posts, HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun updatePost(@PathVariable id: UUID, @RequestBody @Valid postUpdateDTO: PostUpdateDTO): ResponseEntity<Post> {
        val updatedPost = postServiceImpl.updatePost(id, postUpdateDTO)
        return ResponseEntity(updatedPost, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: UUID): ResponseEntity<Void> {
        postServiceImpl.deletePost(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
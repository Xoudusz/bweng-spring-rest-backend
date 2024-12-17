package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.PostCreateDTO
import at.technikum.springrestbackend.dto.PostUpdateDTO
import at.technikum.springrestbackend.entity.Post
import at.technikum.springrestbackend.service.PostServiceImpl
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postServiceImpl: PostServiceImpl
) {


    @PostMapping
    fun createPost(@RequestBody @Valid postCreateDTO: PostCreateDTO): ResponseEntity<Post> {
        return ResponseEntity(postServiceImpl.createPost(postCreateDTO), HttpStatus.CREATED)
    }

    @GetMapping
    fun getAllPosts(): ResponseEntity<List<Post>> {
        val posts = postServiceImpl.getAllPosts()
        return ResponseEntity(posts, HttpStatus.OK)
    }

    @GetMapping("/{userId}")
    fun getPostsByUser(
        @PathVariable userId: UUID,
        authentication: Authentication
    ): ResponseEntity<List<Post>> {
        val viewerUsername = authentication.name // Username of the authenticated user
        println("Fetching posts for user $userId by viewer $viewerUsername")

        val posts = postServiceImpl.getPostsByUser(userId, viewerUsername)
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

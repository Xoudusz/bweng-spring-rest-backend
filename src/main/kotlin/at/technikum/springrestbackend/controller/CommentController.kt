package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.CommentCreateDTO
import at.technikum.springrestbackend.entity.Comment
import at.technikum.springrestbackend.service.CommentServiceImpl
import at.technikum.springrestbackend.service.LikeServiceImpl
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/comments")
class CommentController(private val commentServiceImpl: CommentServiceImpl) {

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    fun getAllPosts(): ResponseEntity<List<Comment>> {
        val comments = commentServiceImpl.getAllComments()
        return ResponseEntity(comments, HttpStatus.OK)
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    fun createComment(@RequestBody @Valid commentCreateDTO: CommentCreateDTO): ResponseEntity<Comment> {
        return ResponseEntity(commentServiceImpl.createComment(commentCreateDTO), HttpStatus.CREATED)
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{commentId}")
    fun deleteComment(@PathVariable commentId: UUID): ResponseEntity<Void> {
        commentServiceImpl.deleteComment(commentId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/post/{postId}")
    fun getCommentsByPost(@PathVariable postId: UUID): ResponseEntity<List<Comment>> {
        val comments = commentServiceImpl.getCommentsByPost(postId)
        return ResponseEntity(comments, HttpStatus.OK)
    }

    @PreAuthorize("#userId == authentication.principal.id or hasAuthority('ADMIN')")
    @GetMapping("/user/{userId}")
    fun getCommentsByUser(@PathVariable userId: UUID): ResponseEntity<List<Comment>> {
        val comments = commentServiceImpl.getCommentsByUser(userId)
        return ResponseEntity(comments, HttpStatus.OK)
    }

}
package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.LikeCreateDTO
import at.technikum.springrestbackend.dto.PostCreateDTO
import at.technikum.springrestbackend.entity.Like
import at.technikum.springrestbackend.entity.Post
import at.technikum.springrestbackend.service.LikeServiceImpl
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/api/likes")
class LikeController(private val likeServiceImpl: LikeServiceImpl) {

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    fun getAllPosts(): ResponseEntity<List<Like>> {
        val likes = likeServiceImpl.getAllLikes()
        return ResponseEntity(likes, HttpStatus.OK)
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    fun createLike(@RequestBody @Valid likeCreateDTO: LikeCreateDTO): ResponseEntity<Like> {
        return ResponseEntity(likeServiceImpl.createLike(likeCreateDTO), HttpStatus.CREATED)
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{likeId}")
    fun deleteLike(@PathVariable likeId: UUID): ResponseEntity<Void> {
        likeServiceImpl.deleteLike(likeId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/post/{postId}")
    fun getLikesByPost(@PathVariable postId: UUID): ResponseEntity<List<Like>> {
        val likes = likeServiceImpl.getLikesByPost(postId)
        return ResponseEntity(likes, HttpStatus.OK)
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/user/{userId}")
    fun getLikesByUser(@PathVariable userId: UUID): ResponseEntity<List<Like>> {
        val likes = likeServiceImpl.getLikesByUser(userId)
        return ResponseEntity(likes, HttpStatus.OK)
    }


}
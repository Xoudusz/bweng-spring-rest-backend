package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.CommentCreateDTO
import at.technikum.springrestbackend.entity.Comment
import at.technikum.springrestbackend.entity.Like
import at.technikum.springrestbackend.exception.PostNotFoundException
import at.technikum.springrestbackend.exception.UserNotFoundException
import at.technikum.springrestbackend.repository.CommentRepository
import at.technikum.springrestbackend.repository.PostRepository
import at.technikum.springrestbackend.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommentServiceImpl @Autowired constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val commentRepository: CommentRepository
) : CommentService {

    @Transactional
    override fun createComment(commentCreateDTO: CommentCreateDTO): Comment {
        val userExists = userRepository.existsById(commentCreateDTO.userId)

        if (!userExists) {
            throw UserNotFoundException("User with ID ${commentCreateDTO.userId} not found")
        }
        val postExists = postRepository.existsById(commentCreateDTO.postId)
        if (!postExists) {
            throw PostNotFoundException("Post with ID ${commentCreateDTO.postId} not found")
        }
        val comment = Comment(
            userId = commentCreateDTO.userId,
            postId = commentCreateDTO.postId,
            content = commentCreateDTO.content
        )

        return commentRepository.save(comment)
    }

    override fun deleteComment(commentId: UUID) {
        if (!commentRepository.existsById(commentId)) {
            throw PostNotFoundException("Post with ID $commentId not found")
        }
        commentRepository.deleteById(commentId)
    }

    override fun getCommentsByPost(postId: UUID): List<Comment> {
        val postExists = postRepository.existsById(postId)
        if (!postExists) {
            throw UserNotFoundException("User with ID $postId not found")
        }
        return commentRepository.findByUserId(postId)
    }

    override fun getCommentsByUser(userId: UUID): List<Comment> {
        val userExists = userRepository.existsById(userId)
        if (!userExists) {
            throw UserNotFoundException("User with ID $userId not found")
        }
        return commentRepository.findByUserId(userId)
    }

    override fun getAllComments(): List<Comment> {
        return commentRepository.findAll()
    }
}
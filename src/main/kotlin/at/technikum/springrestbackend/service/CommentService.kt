package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.CommentCreateDTO
import at.technikum.springrestbackend.entity.Comment
import org.springframework.stereotype.Service
import java.util.*

@Service
interface CommentService {
    fun createComment(commentCreateDTO: CommentCreateDTO): Comment
    fun deleteComment(commentId: UUID)
    fun getCommentsByPost(postId: UUID): List<Comment>
    fun getCommentsByUser(userId: UUID): List<Comment>
    fun getAllComments(): List<Comment>
}
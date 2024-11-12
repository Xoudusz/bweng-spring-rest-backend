package at.technikum.springrestbackend.repository

import at.technikum.springrestbackend.entity.Comment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CommentRepository: JpaRepository<Comment, UUID> {
    fun findByUserId(userId: UUID): List<Comment>
    fun findByPostId(postId: UUID): List<Comment>
}
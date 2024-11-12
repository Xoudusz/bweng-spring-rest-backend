package at.technikum.springrestbackend.repository

import at.technikum.springrestbackend.entity.Like
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface LikeRepository: JpaRepository<Like, UUID> {
    fun findByUserId(userId: UUID): List<Like>
    fun findByPostId(postId: UUID): List<Like>
}
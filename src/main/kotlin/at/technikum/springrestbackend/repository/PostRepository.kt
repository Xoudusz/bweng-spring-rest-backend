package at.technikum.springrestbackend.repository

import at.technikum.springrestbackend.entity.Post
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostRepository : JpaRepository<Post, UUID>{
    fun findByUserId(userId: UUID): List<Post>
}
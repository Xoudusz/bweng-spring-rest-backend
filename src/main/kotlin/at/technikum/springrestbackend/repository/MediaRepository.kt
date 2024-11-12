package at.technikum.springrestbackend.repository

import at.technikum.springrestbackend.entity.Media
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface MediaRepository : JpaRepository<Media, UUID> {
    fun findByPostId(postId: UUID): List<Media>


}

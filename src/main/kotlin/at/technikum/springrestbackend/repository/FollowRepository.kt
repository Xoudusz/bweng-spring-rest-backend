package at.technikum.springrestbackend.repository

import at.technikum.springrestbackend.entity.Follow
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FollowRepository : JpaRepository<Follow, UUID> {
    fun findByFollowerId(followerId: UUID): List<Follow>
    fun findByFollowingId(followingId: UUID): List<Follow>

}

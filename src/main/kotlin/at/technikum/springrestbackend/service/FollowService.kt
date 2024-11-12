package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.FollowDTO
import at.technikum.springrestbackend.entity.Follow
import java.util.UUID

interface FollowService {
    fun followUser(followerId: UUID, followingId: UUID): Follow
    fun unfollowUser(followerId: UUID, followingId: UUID)
    fun getFollowers(userId: UUID): List<FollowDTO>
    fun getFollowing(userId: UUID): List<FollowDTO>
}

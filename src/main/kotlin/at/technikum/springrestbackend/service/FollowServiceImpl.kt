package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.FollowDTO
import at.technikum.springrestbackend.entity.Follow
import at.technikum.springrestbackend.exception.UserNotFoundException
import at.technikum.springrestbackend.repository.FollowRepository
import at.technikum.springrestbackend.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FollowServiceImpl(
    private val followRepository: FollowRepository,
    private val userRepository: UserRepository
) : FollowService {

    override fun followUser(followerId: UUID, followingId: UUID): Follow {
        val follower = userRepository.findById(followerId).orElseThrow {
            UserNotFoundException("Follower with id $followerId not found")
        }
        val following = userRepository.findById(followingId).orElseThrow {
            UserNotFoundException("Following user with id $followingId not found")
        }

        val follow = Follow(follower = follower, following = following)
        return followRepository.save(follow)
    }


    override fun unfollowUser(followerId: UUID, followingId: UUID) {
        val follow = followRepository.findByFollowerId(followerId)
            .find { it.following.id == followingId }
        follow?.let { followRepository.delete(it) }
    }

    override fun getFollowers(userId: UUID): List<FollowDTO> {
        return followRepository.findByFollowingId(userId).map {
            FollowDTO(it.follower.id, it.following.id)
        }
    }

    override fun getFollowing(userId: UUID): List<FollowDTO> {
        return followRepository.findByFollowerId(userId).map {
            FollowDTO(it.follower.id, it.following.id)
        }
    }
}

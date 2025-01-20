package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.entity.Follow
import at.technikum.springrestbackend.entity.User
import at.technikum.springrestbackend.entity.enums.Role
import at.technikum.springrestbackend.exception.notFound.UserNotFoundException
import at.technikum.springrestbackend.repository.FollowRepository
import at.technikum.springrestbackend.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import java.util.*

class FollowServiceImplTest {

    private val followRepository: FollowRepository = mock(FollowRepository::class.java)
    private val userRepository: UserRepository = mock(UserRepository::class.java)
    private val followService = FollowServiceImpl(followRepository, userRepository)

    @Test
    fun `should follow a user`() {
        val followerId = UUID.randomUUID()
        val followingId = UUID.randomUUID()

        val follower = User(id = followerId, username = "follower", country = "Austria", email = "follower@mail.com", password = "FollowerPW1*", role = Role.USER, salutation = "Mr.")
        val following = User(id = followingId, username = "following", country = "Austria", email = "following@mail.com", password = "FollowingPW1*", role = Role.USER, salutation = "Ms.")

        `when`(userRepository.findById(followerId)).thenReturn(Optional.of(follower))
        `when`(userRepository.findById(followingId)).thenReturn(Optional.of(following))
        `when`(followRepository.save(any(Follow::class.java))).thenAnswer { it.arguments[0] }

        val result = followService.followUser(followerId, followingId)

        assertEquals(follower, result.follower)
        assertEquals(following, result.following)
        verify(followRepository, times(1)).save(any(Follow::class.java))
    }

    @Test
    fun `should throw UserNotFoundException when follower does not exist`() {
        val followerId = UUID.randomUUID()
        val followingId = UUID.randomUUID()

        `when`(userRepository.findById(followerId)).thenReturn(Optional.empty())

        assertThrows<UserNotFoundException> {
            followService.followUser(followerId, followingId)
        }
    }

    @Test
    fun `should unfollow a user`() {
        val followerId = UUID.randomUUID()
        val followingId = UUID.randomUUID()

        val follower = User(id = followerId, username = "follower", country = "Austria", email = "follower@mail.com", password = "FollowerPW1*", role = Role.USER, salutation = "Mr.")
        val following = User(id = followingId, username = "following", country = "Austria", email = "following@mail.com", password = "FollowingPW1*", role = Role.USER, salutation = "Ms.")
        val follow = Follow(id = UUID.randomUUID(), follower = follower, following = following)

        `when`(userRepository.findById(followerId)).thenReturn(Optional.of(follower))
        `when`(userRepository.findById(followingId)).thenReturn(Optional.of(following))
        `when`(followRepository.findByFollowerId(followerId)).thenReturn(listOf(follow))

        followService.unfollowUser(followerId, followingId)

        verify(followRepository, times(1)).delete(follow)
    }

    @Test
    fun `should get followers for a user`() {
        val userId = UUID.randomUUID()
        val followerId = UUID.randomUUID()

        val follower = User(id = followerId, username = "follower", country = "Austria", email = "follower@mail.com", password = "FollowerPW1*", role = Role.USER, salutation = "Mr.")
        val following = User(id = userId, username = "following", country = "Austria", email = "following@mail.com", password = "FollowingPW1*", role = Role.USER, salutation = "Ms.")
        val follow = Follow(id = UUID.randomUUID(), follower = follower, following = following)

        `when`(followRepository.findByFollowingId(userId)).thenReturn(listOf(follow))

        val result = followService.getFollowers(userId)

        assertEquals(1, result.size)
        assertEquals(followerId, result[0].followerId)
    }

    @Test
    fun `should get following for a user`(){
        val userId = UUID.randomUUID()
        val followingId = UUID.randomUUID()

        val follower = User(id = userId, username = "follower", country = "Austria", email = "follower@mail.com", password = "FollowerPW1*", role = Role.USER, salutation = "Mr.")
        val following = User(id = followingId, username = "following", country = "Austria", email = "following@mail.com", password = "FollowingPW1*", role = Role.USER, salutation = "Ms.")
        val follow = Follow(id = UUID.randomUUID(), follower = follower, following = following)

        `when`(followRepository.findByFollowerId(userId)).thenReturn(listOf(follow))

        val result = followService.getFollowing(userId)

        assertEquals(1, result.size)
        assertEquals(followingId, result[0].followingId)
    }
}

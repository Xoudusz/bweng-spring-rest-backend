package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.FollowDTO
import at.technikum.springrestbackend.service.FollowService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/follows")
class FollowController(
    private val followService: FollowService
) {

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    fun followUser(@RequestBody followDTO: FollowDTO) =
        followService.followUser(followDTO.followerId, followDTO.followingId)

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping
    fun unfollowUser(@RequestBody followDTO: FollowDTO) =
        followService.unfollowUser(followDTO.followerId, followDTO.followingId)

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/followers/{userId}")
    fun getFollowers(@PathVariable userId: UUID) =
        followService.getFollowers(userId)

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/following/{userId}")
    fun getFollowing(@PathVariable userId: UUID) =
        followService.getFollowing(userId)
}
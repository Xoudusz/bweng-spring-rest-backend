package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.PostCreateDTO
import at.technikum.springrestbackend.dto.PostUpdateDTO
import at.technikum.springrestbackend.entity.Post
import at.technikum.springrestbackend.exception.AccessDeniedException
import at.technikum.springrestbackend.exception.notFound.PostNotFoundException
import at.technikum.springrestbackend.exception.notFound.UserNotFoundException
import at.technikum.springrestbackend.repository.FollowRepository
import at.technikum.springrestbackend.repository.PostRepository
import at.technikum.springrestbackend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PostServiceImpl @Autowired constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val followRepository: FollowRepository
) : PostService {

    @Transactional
    override fun createPost(postCreateDTO: PostCreateDTO): Post {
        val userExists = userRepository.existsById(postCreateDTO.userId)
        if (!userExists) {
            throw UserNotFoundException("User with ID ${postCreateDTO.userId} not found")
        }
        val post = Post(
            userId = postCreateDTO.userId,
            content = postCreateDTO.content
        )
        return postRepository.save(post)
    }

    override fun getPostById(id: UUID): Post {
        return postRepository.findById(id).orElseThrow {
            PostNotFoundException("Post with ID $id not found")
        }
    }

    override fun getAllPosts(): List<Post> {
        return postRepository.findAll()
    }

    override fun updatePost(id: UUID, postUpdateDTO: PostUpdateDTO): Post {
        val existingPost = postRepository.findById(id).orElseThrow {
            PostNotFoundException("Post with ID $id not found")
        }
        return existingPost.copy(content = postUpdateDTO.content).also { postRepository.save(it) }
    }

    @Transactional
    override fun deletePost(id: UUID) {
        if (!postRepository.existsById(id)) {
            throw PostNotFoundException("Post with ID $id not found")
        }
        postRepository.deleteById(id)
    }

    @Transactional
    override fun getPostsByUser(userId: UUID, viewerUsername: String): List<Post> {
        // Fetch the viewer's UUID using the username
        val viewer = userRepository.findByUsername(viewerUsername)
            ?: throw UserNotFoundException("Viewer with username $viewerUsername not found")

        val viewerId = viewer.id // UUID of the authenticated user

        val user = userRepository.findById(userId).orElseThrow {
            UserNotFoundException("User with ID $userId not found")
        }

        // If the user's profile is private, check if the viewer is a follower
        if (user.isPrivate) {
            val isFollower = followRepository.existsByFollowerIdAndFollowingId(viewerId, userId)
            if (!isFollower) {
                throw AccessDeniedException("You do not have permission to view posts from this user")
            }
        }

        // Return posts if access is granted
        return postRepository.findByUserId(userId)
    }

}

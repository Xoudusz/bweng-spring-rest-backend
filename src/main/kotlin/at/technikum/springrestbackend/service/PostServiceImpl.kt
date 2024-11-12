package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.PostCreateDTO
import at.technikum.springrestbackend.dto.PostUpdateDTO
import at.technikum.springrestbackend.entity.Post
import at.technikum.springrestbackend.exception.notFound.PostNotFoundException
import at.technikum.springrestbackend.exception.notFound.UserNotFoundException
import at.technikum.springrestbackend.repository.PostRepository
import at.technikum.springrestbackend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PostServiceImpl @Autowired constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository
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

    override fun getPostsByUser(userId: UUID): List<Post> {
        val userExists = userRepository.existsById(userId)
        if (!userExists) {
            throw UserNotFoundException("User with ID $userId not found")
        }
        return postRepository.findByUserId(userId)
    }
}

package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.entity.Post
import at.technikum.springrestbackend.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PostServiceImpl @Autowired constructor(
    private val postRepository: PostRepository
) : PostService {

    @Transactional
    override fun createPost(post: Post): Post {
        return postRepository.save(post)
    }

    override fun getPostById(id: UUID): Post? {
        return postRepository.findById(id).orElse(null)
    }

    override fun getAllPosts(): List<Post> {
        return postRepository.findAll()
    }

    @Transactional
    override fun updatePost(id: UUID, post: Post): Post? {
        return if (postRepository.existsById(id)) {
            post.id = id
            postRepository.save(post)
        } else {
            null
        }
    }

    @Transactional
    override fun deletePost(id: UUID): Boolean {
        return if (postRepository.existsById(id)) {
            postRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    override fun getPostsByUser(userId: UUID): List<Post> {
        return postRepository.findByUserId(userId)
    }
}

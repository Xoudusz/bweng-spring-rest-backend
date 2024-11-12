package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.LikeCreateDTO
import at.technikum.springrestbackend.entity.Like
import at.technikum.springrestbackend.exception.PostNotFoundException
import at.technikum.springrestbackend.exception.UserNotFoundException
import at.technikum.springrestbackend.repository.LikeRepository
import at.technikum.springrestbackend.repository.PostRepository
import at.technikum.springrestbackend.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class LikeServiceImpl @Autowired constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val likeRepository: LikeRepository
) : LikeService {

    @Transactional
    override fun createLike(likeCreateDTO: LikeCreateDTO): Like {
        val userExists = userRepository.existsById(likeCreateDTO.userId)

        if (!userExists) {
            throw UserNotFoundException("User with ID ${likeCreateDTO.userId} not found")
        }
        val postExists = postRepository.existsById(likeCreateDTO.postId)
        if (!postExists) {
            throw PostNotFoundException("Post with ID ${likeCreateDTO.postId} not found")
        }
        val like = Like(
            userId = likeCreateDTO.userId,
            postId = likeCreateDTO.postId
        )

        return likeRepository.save(like)
    }

    override fun deleteLike(likeId: UUID) {
        if (!likeRepository.existsById(likeId)) {
            throw PostNotFoundException("Post with ID $likeId not found")
        }
        likeRepository.deleteById(likeId)
    }

    override fun getLikesByPost(postId: UUID): List<Like> {
        val postExists = postRepository.existsById(postId)
        if (!postExists) {
            throw UserNotFoundException("User with ID $postId not found")
        }
        return likeRepository.findByUserId(postId)

    }

    override fun getLikesByUser(userId: UUID): List<Like> {
        val userExists = userRepository.existsById(userId)
        if (!userExists) {
            throw UserNotFoundException("User with ID $userId not found")
        }
        return likeRepository.findByUserId(userId)
    }

    override fun getAllLikes(): List<Like> {
        return likeRepository.findAll()
    }
}
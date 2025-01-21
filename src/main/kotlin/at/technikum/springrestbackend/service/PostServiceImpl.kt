package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.PostCreateDTO
import at.technikum.springrestbackend.dto.PostResponseDTO
import at.technikum.springrestbackend.dto.PostUpdateDTO
import at.technikum.springrestbackend.entity.Post
import at.technikum.springrestbackend.exception.notFound.PostNotFoundException
import at.technikum.springrestbackend.exception.notFound.UserNotFoundException
import at.technikum.springrestbackend.repository.FileRepository
import at.technikum.springrestbackend.repository.PostRepository
import at.technikum.springrestbackend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.FileNotFoundException
import java.util.*

@Service
class PostServiceImpl @Autowired constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val fileRepository: FileRepository
) : PostService {

    @Transactional
    override fun createPost(postCreateDTO: PostCreateDTO, username: String): PostResponseDTO {
        val user = userRepository.findByUsername(username)
            ?: throw UserNotFoundException("User with username $username not found")

        val file = postCreateDTO.fileId?.let { fileId ->
            fileRepository.findById(fileId)
                .orElseThrow { FileNotFoundException("File with ID $fileId not found") }
        }

        val post = Post(
            user = user,
            content = postCreateDTO.content,
            file = file
        )

        val savedPost = postRepository.save(post)
        return PostResponseDTO(
            id = savedPost.id,
            content = savedPost.content,
            username = savedPost.user.username,
            createdAt = savedPost.createdAt,
            file = savedPost.file,
            profilePicture = savedPost.user.profilePicture
        )
    }

    override fun getPostById(id: UUID): PostResponseDTO {
        val post = postRepository.findById(id).orElseThrow {
            PostNotFoundException("Post with ID $id not found")
        }
        return PostResponseDTO(
            id = post.id,
            content = post.content,
            username = post.user.username,
            createdAt = post.createdAt,
            file = post.file,
            profilePicture = post.user.profilePicture
        )
    }

    override fun getPostByUsername(username: String): List<PostResponseDTO> {
        return  postRepository.findByUserUsername(username).map { post ->
            PostResponseDTO(
                id = post.id,
                content = post.content,
                username = post.user.username,
                createdAt = post.createdAt,
                file = post.file,
                profilePicture = post.user.profilePicture
            )
        }
    }

    override fun getAllPosts(): List<PostResponseDTO> {
        return postRepository.findAll().map { post ->
            PostResponseDTO(
                id = post.id,
                content = post.content,
                username = post.user.username,
                createdAt = post.createdAt,
                file = post.file,
                profilePicture = post.user.profilePicture
            )
        }
    }

    override fun updatePost(id: UUID, postUpdateDTO: PostUpdateDTO): PostResponseDTO {
        val existingPost = postRepository.findById(id).orElseThrow {
            PostNotFoundException("Post with ID $id not found")
        }
        val updatedPost = existingPost.copy(content = postUpdateDTO.content)
        postRepository.save(updatedPost)
        return PostResponseDTO(
            id = updatedPost.id,
            content = updatedPost.content,
            username = updatedPost.user.username,
            createdAt = updatedPost.createdAt,
            file = updatedPost.file,
            profilePicture = updatedPost.user.profilePicture
        )
    }

    @Transactional
    override fun deletePost(id: UUID) {
        if (!postRepository.existsById(id)) {
            throw PostNotFoundException("Post with ID $id not found")
        }
        postRepository.deleteById(id)
    }

    override fun getPostsByUser(userId: UUID): List<PostResponseDTO> {
        val userExists = userRepository.existsById(userId)
        if (!userExists) {
            throw UserNotFoundException("User with ID $userId not found")
        }
        return postRepository.findByUserId(userId).map { post ->
            PostResponseDTO(
                id = post.id,
                content = post.content,
                username = post.user.username,
                createdAt = post.createdAt,
                file = post.file,
                profilePicture = post.user.profilePicture
            )
        }
    }
}
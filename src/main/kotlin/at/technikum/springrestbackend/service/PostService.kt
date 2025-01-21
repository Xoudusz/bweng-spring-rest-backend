package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.PostCreateDTO
import at.technikum.springrestbackend.dto.PostResponseDTO
import at.technikum.springrestbackend.dto.PostUpdateDTO
import org.springframework.stereotype.Service
import java.util.*

@Service
interface PostService {
    fun createPost(postCreateDTO: PostCreateDTO, username: String): PostResponseDTO
    fun getPostById(id: UUID): PostResponseDTO
    fun getPostByUsername(username: String): List<PostResponseDTO>
    fun getAllPosts(): List<PostResponseDTO>
    fun updatePost(id: UUID, postUpdateDTO: PostUpdateDTO): PostResponseDTO
    fun deletePost(id: UUID)
    fun getPostsByUser(userId: UUID): List<PostResponseDTO>
}

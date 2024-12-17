package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.PostCreateDTO
import at.technikum.springrestbackend.dto.PostUpdateDTO
import at.technikum.springrestbackend.entity.Post
import org.springframework.stereotype.Service
import java.util.*

@Service
interface PostService {
    fun createPost(postCreateDTO: PostCreateDTO): Post
    fun getPostById(id: UUID): Post?
    fun getAllPosts(): List<Post>
    fun updatePost(id: UUID, postUpdateDTO: PostUpdateDTO): Post?
    fun deletePost(id: UUID)

    fun getPostsByUser(userId: UUID, viewerUsername: String): List<Post>
}

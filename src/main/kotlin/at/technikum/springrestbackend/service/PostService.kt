package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.PostUpdateDTO
import at.technikum.springrestbackend.entity.Post
import org.springframework.stereotype.Service
import java.util.*

@Service
interface PostService {
    fun createPost(post: Post): Post
    fun getPostById(id: UUID): Post?
    fun getAllPosts(): List<Post>
    fun updatePost(id: UUID, postUpdateDTO: PostUpdateDTO): Post?
    fun deletePost(id: UUID): Boolean
    fun getPostsByUser(userId: UUID): List<Post>
}

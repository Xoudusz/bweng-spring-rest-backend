package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.LikeCreateDTO
import at.technikum.springrestbackend.entity.Like
import org.springframework.stereotype.Service
import java.util.*

@Service
interface LikeService {
    fun createLike(likeCreateDTO: LikeCreateDTO): Like
    fun deleteLike(likeId: UUID)
    fun getLikesByPost(postId: UUID): List<Like>
    fun getLikesByUser(userId: UUID): List<Like>
    fun getAllLikes(): List<Like>



}
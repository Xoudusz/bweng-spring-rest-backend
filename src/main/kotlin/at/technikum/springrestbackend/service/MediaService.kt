package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.MediaDTO
import at.technikum.springrestbackend.entity.Media
import java.util.UUID

interface MediaService {
    fun addMediaToPost(mediaDTO: MediaDTO): Media
    fun getMediaByPost(postId: UUID): List<MediaDTO>
}

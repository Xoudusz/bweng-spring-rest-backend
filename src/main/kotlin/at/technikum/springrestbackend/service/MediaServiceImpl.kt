package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.dto.MediaDTO
import at.technikum.springrestbackend.entity.Media
import at.technikum.springrestbackend.exception.PostNotFoundException
import at.technikum.springrestbackend.repository.MediaRepository
import at.technikum.springrestbackend.repository.PostRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MediaServiceImpl(
    private val mediaRepository: MediaRepository,
    private val postRepository: PostRepository
) : MediaService {

    override fun addMediaToPost(mediaDTO: MediaDTO): Media {
        val post = postRepository.findById(mediaDTO.postId).orElseThrow {
            PostNotFoundException("Post with id ${mediaDTO.postId} not found")
        }

        val media = Media(
            post = post,
            url = mediaDTO.url,
            type = mediaDTO.type
        )
        return mediaRepository.save(media)
    }

    override fun getMediaByPost(postId: UUID): List<MediaDTO> {
        return mediaRepository.findByPostId(postId).map { media ->
            MediaDTO(media.post.id, media.url, media.type)
        }
    }
}

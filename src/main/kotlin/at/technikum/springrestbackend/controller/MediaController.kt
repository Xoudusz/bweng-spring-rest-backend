package at.technikum.springrestbackend.controller

import at.technikum.springrestbackend.dto.MediaDTO
import at.technikum.springrestbackend.service.MediaService
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/media")
class MediaController(
    private val mediaService: MediaService
) {

    @PostMapping
    fun addMediaToPost(@RequestBody mediaDTO: MediaDTO) = mediaService.addMediaToPost(mediaDTO)

    @GetMapping("/post/{postId}")
    fun getMediaByPost(@PathVariable postId: UUID) = mediaService.getMediaByPost(postId)
}

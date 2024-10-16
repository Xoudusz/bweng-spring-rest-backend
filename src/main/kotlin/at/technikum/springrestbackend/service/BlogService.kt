package at.technikum.springrestbackend.service

import at.technikum.springrestbackend.entity.BlogEntry
import at.technikum.springrestbackend.repository.BlogRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class BlogService(@Autowired private val blogRepository: BlogRepository) {
    fun getPostsByUserId(userId: Long): Optional<BlogEntry> = blogRepository.findById(userId)
    fun createPost(post: BlogEntry): BlogEntry = blogRepository.save(post)
}
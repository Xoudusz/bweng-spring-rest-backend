package at.technikum.springrestbackend.config.permissionEvaluator

import at.technikum.springrestbackend.entity.UserPrincipal
import at.technikum.springrestbackend.repository.PostRepository
import org.springframework.security.access.PermissionEvaluator
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.*

@Component
class PostPermissionEvaluator(
    private val postRepository: PostRepository
) : PermissionEvaluator {

    override fun hasPermission(
        authentication: org.springframework.security.core.Authentication?,
        targetDomainObject: Any?,
        permission: Any?
    ): Boolean {
        val postId = targetDomainObject as UUID
        val userId = (authentication?.principal as UserPrincipal).id
        val isAdmin = authentication.authorities.any { it.authority == "ADMIN" }

        // Allow if the user is admin
        if (isAdmin) {
            return true
        }

        // Otherwise, check if the user owns the post
        val post = postRepository.findById(postId).orElse(null) ?: return false
        return post.user.id == userId
    }

    override fun hasPermission(
        authentication: org.springframework.security.core.Authentication?,
        targetId: Serializable?,
        targetType: String?,
        permission: Any?
    ): Boolean = false // Not used in this case

}

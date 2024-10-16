package at.technikum.springrestbackend.repository

import at.technikum.springrestbackend.entity.BlogEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlogRepository : JpaRepository<BlogEntry, Long> {
}
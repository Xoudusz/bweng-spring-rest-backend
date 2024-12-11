package at.technikum.springrestbackend.repository

import at.technikum.springrestbackend.entity.File
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : JpaRepository<File, Long> {
    fun findByUuid(uuid: String): File?
}

package at.technikum.springrestbackend.entity


import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "files")
data class File(
    @Id
    @Column(nullable = false, unique = true)
    val uuid: String,

    @Column(nullable = false)
    val fileName: String,

    @Column(nullable = false)
    val contentType: String,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    val uploader: String
)

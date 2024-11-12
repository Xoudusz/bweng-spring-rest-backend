package at.technikum.springrestbackend.entity

import jakarta.persistence.*
import java.util.*


@Entity
@Table(name = "follows")
data class Follow(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: UUID = UUID.randomUUID(),

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    val follower: User,  //following

    @ManyToOne
    @JoinColumn(name = "following_id", nullable = false)
    val following: User //being followed
)


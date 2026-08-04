package com.docutesis.backend.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reviews")
class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    var submission: Submission,

    @Column(nullable = false, length = 1000)
    var comment: String,

    @Column(name = "tutor_user", nullable = false, length = 60)
    var tutorUser: String,

    @Column(name = "reviewed_at", nullable = false)
    val reviewedAt: LocalDateTime = LocalDateTime.now()
)
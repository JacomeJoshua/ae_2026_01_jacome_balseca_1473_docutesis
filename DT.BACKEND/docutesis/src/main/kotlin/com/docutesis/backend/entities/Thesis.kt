package com.docutesis.backend.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "theses")
class Thesis(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 150)
    var title: String,

    @Column(length = 500)
    var description: String? = null,

    @Column(name = "repository_url", nullable = false, length = 255)
    var repositoryUrl: String,

    @Column(name = "student_cognito_id", nullable = false, length = 60)
    var studentCognitoId: String,

    @Column(name = "tutor_cognito_id", length = 60)
    var tutorCognitoId: String? = null,

    @Column(nullable = false, length = 30)
    var status: String = "IN_PROGRESS",

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
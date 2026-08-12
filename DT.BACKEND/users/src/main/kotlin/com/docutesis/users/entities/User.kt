package com.docutesis.users.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "cognito_id", nullable = false, unique = true, length = 60)
    val cognitoId: String,

    @Column(nullable = false, length = 100)
    var email: String,

    @Column(name = "name", nullable = false, length = 100)
    var fullName: String,

    @Column(nullable = true, length = 30)
    var role: String? = null,

    @Column(nullable = true, length = 20)
    var phone: String? = null,

    @Column(name = "created_at", nullable = true)
    val createdAt: LocalDateTime? = LocalDateTime.now()
)
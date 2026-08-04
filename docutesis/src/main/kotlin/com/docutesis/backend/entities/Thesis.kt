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

    @Column(name = "owner_user", nullable = false, length = 60)
    var ownerUser: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
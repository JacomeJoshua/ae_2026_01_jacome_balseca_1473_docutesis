package com.docutesis.backend.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "submissions")
class Submission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thesis_id", nullable = false)
    var thesis: Thesis,

    @Column(name = "file_url", nullable = false, length = 255)
    var fileUrl: String,

    @Column(name = "github_commit_hash", length = 100)
    var githubCommitHash: String? = null,

    @Column(name = "uploaded_at", nullable = false)
    val uploadedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "submission", cascade = [CascadeType.ALL], orphanRemoval = true)
    val statuses: MutableList<SubmissionStatus> = mutableListOf()
)
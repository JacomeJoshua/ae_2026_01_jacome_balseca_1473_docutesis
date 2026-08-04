package com.docutesis.backend.entities

import jakarta.persistence.*
import java.time.LocalDateTime

enum class ProgressStatus {
    PENDING,
    UNDER_REVIEW,
    APPROVED,
    CHANGES_REQUESTED
}

@Entity
@Table(name = "submission_statuses")
class SubmissionStatus(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    var submission: Submission,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: ProgressStatus,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PrePersist
    @PreUpdate
    fun onUpdate() {
        this.updatedAt = LocalDateTime.now()
    }
}
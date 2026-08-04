package com.docutesis.backend.dtos

import com.docutesis.backend.entities.ProgressStatus
import java.time.LocalDateTime

data class SubmissionCreateRequest(
    val thesisId: Long,
    val fileUrl: String,
    val githubCommitHash: String? = null
)

data class SubmissionResponse(
    val id: Long,
    val thesisId: Long,
    val fileUrl: String,
    val githubCommitHash: String?,
    val currentStatus: ProgressStatus? = null,
    val uploadedAt: LocalDateTime? = null
)

data class SubmissionHistoryResponse(
    val submissionId: Long,
    val fileUrl: String,
    val githubCommitHash: String?,
    val uploadedAt: LocalDateTime,
    val currentStatus: ProgressStatus?,
    val approvedAt: LocalDateTime?,
    val statusHistory: List<SubmissionStatusResponse>,
    val reviews: List<ReviewResponse>
)
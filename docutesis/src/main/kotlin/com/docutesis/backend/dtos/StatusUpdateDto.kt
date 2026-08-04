package com.docutesis.backend.dtos

import com.docutesis.backend.entities.ProgressStatus
import java.time.LocalDateTime

data class StatusUpdateRequest(
    val status: ProgressStatus
)

data class SubmissionStatusResponse(
    val id: Long,
    val submissionId: Long,
    val status: ProgressStatus,
    val updatedAt: LocalDateTime
)
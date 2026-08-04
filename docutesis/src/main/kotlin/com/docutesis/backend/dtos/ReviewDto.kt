package com.docutesis.backend.dtos

import java.time.LocalDateTime

data class ReviewCreateRequest(
    val submissionId: Long,
    val comment: String
)

data class ReviewResponse(
    val id: Long,
    val submissionId: Long,
    val comment: String,
    val reviewedAt: LocalDateTime
)
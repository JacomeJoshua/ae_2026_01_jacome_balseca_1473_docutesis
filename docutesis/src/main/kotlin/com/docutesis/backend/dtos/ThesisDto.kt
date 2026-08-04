package com.docutesis.backend.dtos

import java.time.LocalDateTime

data class ThesisCreateRequest(
    val title: String,
    val description: String? = null
)

data class ThesisResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val ownerUser: String? = null,
    val createdAt: LocalDateTime? = null
)

data class ThesisHistoryResponse(
    val thesisId: Long,
    val title: String,
    val description: String?,
    val createdAt: LocalDateTime,
    val submissions: List<SubmissionHistoryResponse>
)
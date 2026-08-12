package com.docutesis.backend.dtos

import java.time.LocalDateTime

data class ThesisCreateRequest(
    val title: String,
    val description: String? = null,
    val repositoryUrl: String
)

data class ThesisResponse(
    val id: Long,
    val title: String,
    val description: String?,
    val repositoryUrl: String,
    val studentCognitoId: String,
    val tutorCognitoId: String?,
    val status: String,
    val createdAt: LocalDateTime
)

data class ThesisHistoryResponse(
    val thesisId: Long,
    val title: String,
    val description: String?,
    val repositoryUrl: String,
    val studentCognitoId: String,
    val tutorCognitoId: String?,
    val status: String,
    val createdAt: LocalDateTime,
    val submissions: List<SubmissionHistoryResponse>
)
package com.docutesis.backend.services

import com.docutesis.backend.dtos.*
import com.docutesis.backend.entities.ProgressStatus
import com.docutesis.backend.entities.Thesis
import com.docutesis.backend.exceptions.ResourceNotFoundException
import com.docutesis.backend.repositories.ReviewRepository
import com.docutesis.backend.repositories.SubmissionRepository
import com.docutesis.backend.repositories.SubmissionStatusRepository
import com.docutesis.backend.repositories.ThesisRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ThesisService(
    private val thesisRepository: ThesisRepository,
    private val submissionRepository: SubmissionRepository,
    private val submissionStatusRepository: SubmissionStatusRepository,
    private val reviewRepository: ReviewRepository
) {

    @Transactional
    fun createThesis(request: ThesisCreateRequest, username: String): ThesisResponse {
        val thesis = Thesis(
            title = request.title,
            description = request.description,
            ownerUser = username
        )
        val saved = thesisRepository.save(thesis)
        return ThesisResponse(
            id = saved.id!!,
            title = saved.title,
            description = saved.description,
            ownerUser = saved.ownerUser,
            createdAt = saved.createdAt
        )
    }

    @Transactional(readOnly = true)
    fun getThesisByOwner(username: String): ThesisResponse {
        val thesis = thesisRepository.findByOwnerUser(username)
            .orElseThrow { ResourceNotFoundException("No se encontró una tesis asociada al usuario: $username") }
        return ThesisResponse(
            id = thesis.id!!,
            title = thesis.title,
            description = thesis.description,
            ownerUser = thesis.ownerUser,
            createdAt = thesis.createdAt
        )
    }

    @Transactional(readOnly = true)
    fun getThesisHistory(thesisId: Long): ThesisHistoryResponse {
        val thesis = thesisRepository.findById(thesisId)
            .orElseThrow { ResourceNotFoundException("Tesis no encontrada con ID: $thesisId") }

        val submissions = submissionRepository.findByThesisId(thesisId)

        val submissionDTOs = submissions.map { submission ->
            val statusList = submissionStatusRepository.findBySubmissionId(submission.id!!)
            val reviewList = reviewRepository.findBySubmissionId(submission.id!!)

            val currentStatus = statusList.lastOrNull()?.status
            val approvedAt = statusList.firstOrNull { it.status == ProgressStatus.APPROVED }?.updatedAt

            SubmissionHistoryResponse(
                submissionId = submission.id!!,
                fileUrl = submission.fileUrl,
                githubCommitHash = submission.githubCommitHash,
                uploadedAt = submission.uploadedAt,
                currentStatus = currentStatus,
                approvedAt = approvedAt,
                statusHistory = statusList.map {
                    SubmissionStatusResponse(
                        id = it.id!!,
                        submissionId = submission.id!!,
                        status = it.status,
                        updatedAt = it.updatedAt
                    )
                },
                reviews = reviewList.map {
                    ReviewResponse(
                        id = it.id!!,
                        submissionId = submission.id!!,
                        comment = it.comment,
                        reviewedAt = it.reviewedAt
                    )
                }
            )
        }

        return ThesisHistoryResponse(
            thesisId = thesis.id!!,
            title = thesis.title,
            description = thesis.description,
            createdAt = thesis.createdAt,
            submissions = submissionDTOs
        )
    }
}
package com.docutesis.backend.services

import com.docutesis.backend.dtos.*
import com.docutesis.backend.entities.ProgressStatus
import com.docutesis.backend.entities.Submission
import com.docutesis.backend.entities.SubmissionStatus
import com.docutesis.backend.exceptions.AccessDeniedPropertyException
import com.docutesis.backend.exceptions.IntegrityConstraintException
import com.docutesis.backend.exceptions.ResourceNotFoundException
import com.docutesis.backend.repositories.ReviewRepository
import com.docutesis.backend.repositories.SubmissionRepository
import com.docutesis.backend.repositories.SubmissionStatusRepository
import com.docutesis.backend.repositories.ThesisRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SubmissionService(
    private val submissionRepository: SubmissionRepository,
    private val thesisRepository: ThesisRepository,
    private val reviewRepository: ReviewRepository,
    private val submissionStatusRepository: SubmissionStatusRepository
) {

    @Transactional
    fun createSubmission(request: SubmissionCreateRequest, username: String): SubmissionResponse {
        val thesis = thesisRepository.findById(request.thesisId)
            .orElseThrow { ResourceNotFoundException("Tesis no encontrada con ID: ${request.thesisId}") }

        if (thesis.ownerUser != username) {
            throw AccessDeniedPropertyException("No eres el propietario de esta tesis")
        }

        val submission = Submission(
            thesis = thesis,
            fileUrl = request.fileUrl,
            githubCommitHash = request.githubCommitHash
        )
        val savedSubmission = submissionRepository.save(submission)

        val initialStatus = SubmissionStatus(
            submission = savedSubmission,
            status = ProgressStatus.PENDING
        )
        submissionStatusRepository.save(initialStatus)

        return SubmissionResponse(
            id = savedSubmission.id!!,
            thesisId = thesis.id!!,
            fileUrl = savedSubmission.fileUrl,
            githubCommitHash = savedSubmission.githubCommitHash,
            currentStatus = ProgressStatus.PENDING,
            uploadedAt = savedSubmission.uploadedAt
        )
    }

    @Transactional(readOnly = true)
    fun getPendingSubmissions(): List<SubmissionResponse> {
        val pendingStatuses = submissionStatusRepository.findByStatus(ProgressStatus.PENDING)
        return pendingStatuses.map { status ->
            val sub = status.submission
            SubmissionResponse(
                id = sub.id!!,
                thesisId = sub.thesis.id!!,
                fileUrl = sub.fileUrl,
                githubCommitHash = sub.githubCommitHash,
                currentStatus = status.status,
                uploadedAt = sub.uploadedAt
            )
        }
    }

    @Transactional(readOnly = true)
    fun getApprovedSubmissions(): List<SubmissionResponse> {
        val approvedStatuses = submissionStatusRepository.findByStatus(ProgressStatus.APPROVED)
        return approvedStatuses.map { status ->
            val sub = status.submission
            SubmissionResponse(
                id = sub.id!!,
                thesisId = sub.thesis.id!!,
                fileUrl = sub.fileUrl,
                githubCommitHash = sub.githubCommitHash,
                currentStatus = status.status,
                uploadedAt = sub.uploadedAt
            )
        }
    }

    @Transactional
    fun deleteSubmission(submissionId: Long, username: String) {
        val submission = submissionRepository.findById(submissionId)
            .orElseThrow { ResourceNotFoundException("Avance no encontrado con ID: $submissionId") }

        if (submission.thesis.ownerUser != username) {
            throw AccessDeniedPropertyException("No tienes permiso para eliminar este avance")
        }

        val hasReviews = reviewRepository.existsBySubmissionId(submissionId)
        if (hasReviews) {
            throw IntegrityConstraintException("No se puede eliminar el avance porque ya contiene revisiones registradas por el tutor")
        }

        val statuses = submissionStatusRepository.findBySubmissionId(submissionId)
        if (statuses.isNotEmpty()) {
            submissionStatusRepository.deleteAll(statuses)
        }

        submissionRepository.delete(submission)
    }
}
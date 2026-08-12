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
            .orElseThrow { ResourceNotFoundException("Thesis not found with ID: ${request.thesisId}") }

        // 1. Validar que la tesis no esté aprobada totalmente
        if (thesis.status == "APPROVED") {
            throw IntegrityConstraintException("Thesis is already APPROVED. No further submissions allowed.")
        }

        // 2. Validar que el estudiante autenticado sea el dueño de la tesis
        if (thesis.studentCognitoId != username) {
            throw AccessDeniedPropertyException("You are not the assigned student for this thesis.")
        }

        // 3. Validar existencia de la submission previa si se indica previousSubmissionId
        request.previousSubmissionId?.let { prevId ->
            val prevSubmission = submissionRepository.findById(prevId)
                .orElseThrow { ResourceNotFoundException("Previous submission not found with ID: $prevId") }

            if (prevSubmission.thesis.id != thesis.id) {
                throw IntegrityConstraintException("The previous submission belongs to a different thesis.")
            }
        }

        val submission = Submission(
            thesis = thesis,
            commitUrl = request.commitUrl,
            previousSubmissionId = request.previousSubmissionId
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
            commitUrl = savedSubmission.commitUrl,
            previousSubmissionId = savedSubmission.previousSubmissionId,
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
                commitUrl = sub.commitUrl,
                previousSubmissionId = sub.previousSubmissionId,
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
                commitUrl = sub.commitUrl,
                previousSubmissionId = sub.previousSubmissionId,
                currentStatus = status.status,
                uploadedAt = sub.uploadedAt
            )
        }
    }

    @Transactional
    fun deleteSubmission(submissionId: Long, username: String) {
        val submission = submissionRepository.findById(submissionId)
            .orElseThrow { ResourceNotFoundException("Submission not found with ID: $submissionId") }

        if (submission.thesis.status == "APPROVED") {
            throw IntegrityConstraintException("Thesis is APPROVED. Submissions cannot be deleted.")
        }

        if (submission.thesis.studentCognitoId != username) {
            throw AccessDeniedPropertyException("You do not have permission to delete this submission.")
        }

        val hasReviews = reviewRepository.existsBySubmissionId(submissionId)
        if (hasReviews) {
            throw IntegrityConstraintException("Cannot delete submission because it already contains tutor reviews.")
        }

        val statuses = submissionStatusRepository.findBySubmissionId(submissionId)
        if (statuses.isNotEmpty()) {
            submissionStatusRepository.deleteAll(statuses)
        }

        submissionRepository.delete(submission)
    }
}
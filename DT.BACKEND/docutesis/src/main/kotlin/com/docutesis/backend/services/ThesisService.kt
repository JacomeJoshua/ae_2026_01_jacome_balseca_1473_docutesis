package com.docutesis.backend.services

import com.docutesis.backend.dtos.*
import com.docutesis.backend.entities.ProgressStatus
import com.docutesis.backend.entities.Thesis
import com.docutesis.backend.exceptions.AccessDeniedPropertyException
import com.docutesis.backend.exceptions.ResourceNotFoundException
import com.docutesis.backend.repositories.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ThesisService(
    private val thesisRepository: ThesisRepository,
    private val submissionRepository: SubmissionRepository,
    private val reviewRepository: ReviewRepository,
    private val submissionStatusRepository: SubmissionStatusRepository
) {

    @Transactional
    fun createThesis(request: ThesisCreateRequest, studentCognitoId: String): ThesisResponse {
        val thesis = Thesis(
            title = request.title,
            description = request.description,
            repositoryUrl = request.repositoryUrl,
            studentCognitoId = studentCognitoId
        )
        val savedThesis = thesisRepository.save(thesis)

        return savedThesis.toResponse()
    }

    @Transactional(readOnly = true)
    fun getThesisByStudent(studentCognitoId: String): ThesisResponse {
        val thesis = thesisRepository.findByStudentCognitoId(studentCognitoId)
            .orElseThrow { ResourceNotFoundException("No thesis found for student: $studentCognitoId") }

        return thesis.toResponse()
    }

    @Transactional(readOnly = true)
    fun getThesesByTutor(tutorCognitoId: String): List<ThesisResponse> {
        val theses = thesisRepository.findByTutorCognitoId(tutorCognitoId)
        return theses.map { it.toResponse() }
    }

    @Transactional(readOnly = true)
    fun getThesisHistory(thesisId: Long, userCognitoId: String, roles: List<String>): ThesisHistoryResponse {
        val thesis = thesisRepository.findById(thesisId)
            .orElseThrow { ResourceNotFoundException("Thesis not found with ID: $thesisId") }

        if (roles.contains("ROLE_STUDENT") && thesis.studentCognitoId != userCognitoId) {
            throw AccessDeniedPropertyException("You are not authorized to view this thesis history")
        }

        if (roles.contains("ROLE_TUTOR") && thesis.tutorCognitoId != userCognitoId) {
            throw AccessDeniedPropertyException("You are not authorized to view this thesis history")
        }

        val submissions = submissionRepository.findByThesisId(thesisId)

        val submissionDTOs = submissions.map { submission ->
            val subId = submission.id!!
            val reviewList = reviewRepository.findBySubmissionId(subId)
            val statuses = submissionStatusRepository.findBySubmissionId(subId)

            val currentStatus = statuses.maxByOrNull { it.updatedAt }?.status
            val approvedAt = statuses.firstOrNull { it.status == ProgressStatus.APPROVED }?.updatedAt

            SubmissionHistoryResponse(
                submissionId = subId,
                commitUrl = submission.commitUrl,
                previousSubmissionId = submission.previousSubmissionId,
                uploadedAt = submission.uploadedAt,
                currentStatus = currentStatus,
                approvedAt = approvedAt,
                statusHistory = statuses.map { status ->
                    SubmissionStatusResponse(
                        id = status.id!!,
                        submissionId = subId,
                        status = status.status,
                        updatedAt = status.updatedAt
                    )
                },
                reviews = reviewList.map { review ->
                    ReviewResponse(
                        id = review.id!!,
                        submissionId = subId,
                        comment = review.comment,
                        reviewedAt = review.reviewedAt
                    )
                }
            )
        }

        return ThesisHistoryResponse(
            thesisId = thesis.id!!,
            title = thesis.title,
            description = thesis.description,
            repositoryUrl = thesis.repositoryUrl,
            studentCognitoId = thesis.studentCognitoId,
            tutorCognitoId = thesis.tutorCognitoId,
            status = thesis.status,
            createdAt = thesis.createdAt,
            submissions = submissionDTOs
        )
    }

    @Transactional
    fun assignTutor(thesisId: Long, tutorCognitoId: String): ThesisResponse {
        val thesis = thesisRepository.findById(thesisId)
            .orElseThrow { ResourceNotFoundException("Thesis not found with ID: $thesisId") }

        thesis.tutorCognitoId = tutorCognitoId
        val updatedThesis = thesisRepository.save(thesis)

        return updatedThesis.toResponse()
    }

    @Transactional
    fun approveThesis(thesisId: Long, tutorUsername: String): ThesisResponse {
        val thesis = thesisRepository.findById(thesisId)
            .orElseThrow { ResourceNotFoundException("Thesis not found with ID: $thesisId") }

        if (thesis.tutorCognitoId != tutorUsername) {
            throw AccessDeniedPropertyException("You are not the assigned tutor for this thesis")
        }

        thesis.status = "APPROVED"
        val updatedThesis = thesisRepository.save(thesis)

        return updatedThesis.toResponse()
    }

    private fun Thesis.toResponse() = ThesisResponse(
        id = this.id!!,
        title = this.title,
        description = this.description,
        repositoryUrl = this.repositoryUrl,
        studentCognitoId = this.studentCognitoId,
        tutorCognitoId = this.tutorCognitoId,
        status = this.status,
        createdAt = this.createdAt
    )
}
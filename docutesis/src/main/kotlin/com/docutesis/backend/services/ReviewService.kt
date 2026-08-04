package com.docutesis.backend.services

import com.docutesis.backend.dtos.*
import com.docutesis.backend.entities.Review
import com.docutesis.backend.entities.SubmissionStatus
import com.docutesis.backend.exceptions.ResourceNotFoundException
import com.docutesis.backend.repositories.ReviewRepository
import com.docutesis.backend.repositories.SubmissionRepository
import com.docutesis.backend.repositories.SubmissionStatusRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val submissionRepository: SubmissionRepository,
    private val submissionStatusRepository: SubmissionStatusRepository
) {

    @Transactional
    fun addReview(submissionId: Long, request: ReviewCreateRequest, tutorUsername: String): ReviewResponse {
        val submission = submissionRepository.findById(submissionId)
            .orElseThrow { ResourceNotFoundException("Avance no encontrado con ID: $submissionId") }

        val review = Review(
            submission = submission,
            comment = request.comment,
            tutorUser = tutorUsername
        )
        val savedReview = reviewRepository.save(review)

        return ReviewResponse(
            id = savedReview.id!!,
            submissionId = submission.id!!,
            comment = savedReview.comment,
            reviewedAt = savedReview.reviewedAt
        )
    }

    @Transactional
    fun updateSubmissionStatus(submissionId: Long, request: StatusUpdateRequest): SubmissionStatusResponse {
        val submission = submissionRepository.findById(submissionId)
            .orElseThrow { ResourceNotFoundException("Avance no encontrado con ID: $submissionId") }

        val newStatus = SubmissionStatus(
            submission = submission,
            status = request.status
        )
        val savedStatus = submissionStatusRepository.save(newStatus)

        return SubmissionStatusResponse(
            id = savedStatus.id!!,
            submissionId = submission.id!!,
            status = savedStatus.status,
            updatedAt = savedStatus.updatedAt
        )
    }

    @Transactional(readOnly = true)
    fun getReviewsBySubmission(submissionId: Long): List<ReviewResponse> {
        val reviews = reviewRepository.findBySubmissionId(submissionId)
        return reviews.map { review ->
            ReviewResponse(
                id = review.id!!,
                submissionId = submissionId,
                comment = review.comment,
                reviewedAt = review.reviewedAt
            )
        }
    }
}
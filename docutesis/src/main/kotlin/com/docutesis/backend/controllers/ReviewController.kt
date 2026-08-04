package com.docutesis.backend.controllers

import com.docutesis.backend.dtos.ReviewCreateRequest
import com.docutesis.backend.dtos.ReviewResponse
import com.docutesis.backend.dtos.StatusUpdateRequest
import com.docutesis.backend.dtos.SubmissionStatusResponse
import com.docutesis.backend.services.ReviewService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
class ReviewController(
    private val reviewService: ReviewService
) {

    @PostMapping("/avances/{submissionId}/revisiones")
    @PreAuthorize("hasRole('TUTOR')")
    fun addReview(
        @PathVariable submissionId: Long,
        @RequestBody request: ReviewCreateRequest,
        authentication: Authentication
    ): ResponseEntity<ReviewResponse> {
        val response = reviewService.addReview(submissionId, request, authentication.name)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/avances/{id}/estado")
    @PreAuthorize("hasRole('TUTOR')")
    fun updateSubmissionStatus(
        @PathVariable id: Long,
        @RequestBody request: StatusUpdateRequest
    ): ResponseEntity<SubmissionStatusResponse> {
        val response = reviewService.updateSubmissionStatus(id, request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/avances/{submissionId}/revisiones")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'TUTOR')")
    fun getReviewsBySubmission(
        @PathVariable submissionId: Long
    ): ResponseEntity<List<ReviewResponse>> {
        val response = reviewService.getReviewsBySubmission(submissionId)
        return ResponseEntity.ok(response)
    }
}
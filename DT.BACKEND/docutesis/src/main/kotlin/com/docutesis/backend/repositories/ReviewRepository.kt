package com.docutesis.backend.repositories

import com.docutesis.backend.entities.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {
    fun findBySubmissionId(submissionId: Long): List<Review>
    fun existsBySubmissionId(submissionId: Long): Boolean
}
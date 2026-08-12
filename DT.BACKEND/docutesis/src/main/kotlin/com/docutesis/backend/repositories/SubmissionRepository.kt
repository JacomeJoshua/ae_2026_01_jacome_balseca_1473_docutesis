package com.docutesis.backend.repositories

import com.docutesis.backend.entities.Submission
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubmissionRepository : JpaRepository<Submission, Long> {
    fun findByThesisId(thesisId: Long): List<Submission>
}
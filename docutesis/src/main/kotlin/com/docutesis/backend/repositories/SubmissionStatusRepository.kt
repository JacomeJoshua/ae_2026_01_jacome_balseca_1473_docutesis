package com.docutesis.backend.repositories

import com.docutesis.backend.entities.ProgressStatus
import com.docutesis.backend.entities.SubmissionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubmissionStatusRepository : JpaRepository<SubmissionStatus, Long> {
    fun findBySubmissionId(submissionId: Long): List<SubmissionStatus>
    fun findByStatus(status: ProgressStatus): List<SubmissionStatus>
    fun existsBySubmissionId(submissionId: Long): Boolean
}
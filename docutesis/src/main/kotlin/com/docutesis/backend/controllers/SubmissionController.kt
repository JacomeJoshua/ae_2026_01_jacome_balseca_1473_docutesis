package com.docutesis.backend.controllers

import com.docutesis.backend.dtos.SubmissionCreateRequest
import com.docutesis.backend.dtos.SubmissionResponse
import com.docutesis.backend.services.SubmissionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/avances")
class SubmissionController(
    private val submissionService: SubmissionService
) {

    @PostMapping
    @PreAuthorize("hasRole('ESTUDIANTE')")
    fun createSubmission(
        @RequestBody request: SubmissionCreateRequest,
        authentication: Authentication
    ): ResponseEntity<SubmissionResponse> {
        val response = submissionService.createSubmission(request, authentication.name)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('TUTOR', 'ESTUDIANTE')")
    fun getPendingSubmissions(): ResponseEntity<List<SubmissionResponse>> {
        val response = submissionService.getPendingSubmissions()
        return ResponseEntity.ok(response)
    }

    @GetMapping("/aprobados")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'TUTOR')")
    fun getApprovedSubmissions(): ResponseEntity<List<SubmissionResponse>> {
        val response = submissionService.getApprovedSubmissions()
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    fun deleteSubmission(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<Void> {
        submissionService.deleteSubmission(id, authentication.name)
        return ResponseEntity.noContent().build()
    }
}
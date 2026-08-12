package com.docutesis.backend.controllers

import com.docutesis.backend.dtos.ThesisCreateRequest
import com.docutesis.backend.dtos.ThesisHistoryResponse
import com.docutesis.backend.dtos.ThesisResponse
import com.docutesis.backend.services.ThesisService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/theses")
class ThesisController(
    private val thesisService: ThesisService
) {

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    fun createThesis(
        @RequestBody request: ThesisCreateRequest,
        authentication: Authentication
    ): ResponseEntity<ThesisResponse> {
        val response = thesisService.createThesis(request, authentication.name)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    fun getMyThesis(authentication: Authentication): ResponseEntity<ThesisResponse> {
        val response = thesisService.getThesisByStudent(authentication.name)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/tutor/me")
    @PreAuthorize("hasRole('TUTOR')")
    fun getMyAssignedTheses(authentication: Authentication): ResponseEntity<List<ThesisResponse>> {
        val response = thesisService.getThesesByTutor(authentication.name)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('STUDENT', 'TUTOR', 'ADMIN')")
    fun getThesisHistory(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<ThesisHistoryResponse> {
        val roles = authentication.authorities.mapNotNull { it.authority }
        val response = thesisService.getThesisHistory(id, authentication.name, roles)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}/assign-tutor")
    @PreAuthorize("hasRole('ADMIN')")
    fun assignTutor(
        @PathVariable id: Long,
        @RequestParam tutorCognitoId: String
    ): ResponseEntity<ThesisResponse> {
        val response = thesisService.assignTutor(id, tutorCognitoId)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('TUTOR')")
    fun approveThesis(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<ThesisResponse> {
        val response = thesisService.approveThesis(id, authentication.name)
        return ResponseEntity.ok(response)
    }
}
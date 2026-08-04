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
@RequestMapping("/tesis")
class ThesisController(
    private val thesisService: ThesisService
) {

    @PostMapping
    @PreAuthorize("hasRole('ESTUDIANTE')")
    fun createThesis(
        @RequestBody request: ThesisCreateRequest,
        authentication: Authentication
    ): ResponseEntity<ThesisResponse> {
        val response = thesisService.createThesis(request, authentication.name)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    fun getMyThesis(authentication: Authentication): ResponseEntity<ThesisResponse> {
        val response = thesisService.getThesisByOwner(authentication.name)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}/historial")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'TUTOR')")
    fun getThesisHistory(@PathVariable id: Long): ResponseEntity<ThesisHistoryResponse> {
        val response = thesisService.getThesisHistory(id)
        return ResponseEntity.ok(response)
    }
}
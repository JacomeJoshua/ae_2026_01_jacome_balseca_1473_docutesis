package com.docutesis.backend.repositories

import com.docutesis.backend.entities.Thesis
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ThesisRepository : JpaRepository<Thesis, Long> {
    fun findByOwnerUser(ownerUser: String): Optional<Thesis>
}
package com.example.ddd.infrastructure.adapter.persistence

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface OrderJpaRepository : JpaRepository<OrderEntity, UUID> {
    fun findByCustomerId(customerId: UUID): List<OrderEntity>
}
package com.example.ddd.infrastructure.adapter.persistence

import com.example.ddd.domain.model.CustomerId
import com.example.ddd.domain.model.Order
import com.example.ddd.domain.model.OrderId
import com.example.ddd.domain.port.OrderRepository
import java.util.Optional
import java.util.UUID

class OrderRepositoryAdapter(private val jpaRepository: OrderJpaRepository) : OrderRepository {

    override fun save(order: Order): Order {
        val entity = order.toEntity()
        val saved = jpaRepository.save(entity)
        return saved.toDomain()
    }

    override fun findById(id: OrderId): Optional<Order> {
        return jpaRepository.findById(id.value).map { it.toDomain() }
    }

    override fun findByCustomerId(customerId: CustomerId): List<Order> {
        return jpaRepository.findByCustomerId(customerId.value).map { it.toDomain() }
    }

    override fun delete(id: OrderId): Boolean {
        return if (jpaRepository.existsById(id.value)) {
            jpaRepository.deleteById(id.value)
            true
        } else {
            false
        }
    }
}
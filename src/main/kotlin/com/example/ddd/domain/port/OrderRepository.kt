package com.example.ddd.domain.port

import com.example.ddd.domain.model.CustomerId
import com.example.ddd.domain.model.Order
import com.example.ddd.domain.model.OrderId
import java.util.Optional

interface OrderRepository {
    fun save(order: Order): Order
    fun findById(id: OrderId): Optional<Order>
    fun findByCustomerId(customerId: CustomerId): List<Order>
    fun delete(id: OrderId): Boolean
}
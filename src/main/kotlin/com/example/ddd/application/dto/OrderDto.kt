package com.example.ddd.application.dto

import com.example.ddd.domain.model.Order
import com.example.ddd.domain.model.OrderItem
import com.example.ddd.domain.model.OrderStatus
import java.util.UUID

data class CreateOrderRequest(
    val customerId: UUID
)

data class AddItemRequest(
    val productId: UUID,
    val productName: String,
    val quantity: Int,
    val price: Double
)

data class OrderResponse(
    val id: UUID,
    val customerId: UUID,
    val items: List<OrderItemResponse>,
    val status: String,
    val createdAt: Long,
    val totalAmount: Double
)

data class OrderItemResponse(
    val productId: UUID,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val subtotal: Double
)

fun Order.toResponse(): OrderResponse = OrderResponse(
    id = id.value,
    customerId = customerId.value,
    items = items.map { it.toResponse() },
    status = status.name,
    createdAt = createdAt,
    totalAmount = totalAmount.amount
)

fun OrderItem.toResponse(): OrderItemResponse = OrderItemResponse(
    productId = productId.value,
    productName = productName,
    quantity = quantity,
    price = price.amount,
    subtotal = subtotal.amount
)
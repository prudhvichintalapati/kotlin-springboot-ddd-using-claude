package com.example.ddd.domain.model

import java.util.UUID

data class Order(
    val id: OrderId,
    val customerId: CustomerId,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val createdAt: Long,
    val totalAmount: Money
) {
    fun addItem(item: OrderItem): Order = copy(items = items + item)

    fun removeItem(productId: ProductId): Order = copy(items = items.filter { it.productId != productId })

    fun confirm(): Order = when (status) {
        OrderStatus.PENDING -> copy(status = OrderStatus.CONFIRMED)
        else -> this
    }

    fun cancel(): Order = when (status) {
        OrderStatus.PENDING, OrderStatus.CONFIRMED -> copy(status = OrderStatus.CANCELLED)
        else -> this
    }

    fun calculateTotal(): Money = Money(items.sumOf { it.price.amount * it.quantity })
}

@JvmInline
value class OrderId(val value: UUID)

@JvmInline
value class CustomerId(val value: UUID)

@JvmInline
value class ProductId(val value: UUID)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
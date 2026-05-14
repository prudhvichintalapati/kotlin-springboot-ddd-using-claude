package com.example.ddd.infrastructure.adapter.persistence

import com.example.ddd.domain.model.*
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "orders")
data class OrderEntity(
    @Id val id: UUID,
    val customerId: UUID,
    @Enumerated(EnumType.STRING) val status: OrderStatus,
    val createdAt: Long,
    val totalAmount: Double,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    val items: List<OrderItemEntity> = emptyList()
)

@Entity
@Table(name = "order_items")
data class OrderItemEntity(
    @Id @GeneratedValue val id: Long? = null,
    val productId: UUID,
    val productName: String,
    val quantity: Int,
    val price: Double,
    @Column(name = "order_id") val orderId: UUID
)

fun OrderEntity.toDomain(): Order = Order(
    id = OrderId(id),
    customerId = CustomerId(customerId),
    items = items.map { it.toDomain() },
    status = status,
    createdAt = createdAt,
    totalAmount = Money(totalAmount)
)

fun OrderItemEntity.toDomain(): OrderItem = OrderItem(
    productId = ProductId(productId),
    productName = productName,
    quantity = quantity,
    price = Money(price)
)

fun Order.toEntity(): OrderEntity = OrderEntity(
    id = id.value,
    customerId = customerId.value,
    status = status,
    createdAt = createdAt,
    totalAmount = totalAmount.amount,
    items = items.map { it.toEntity(id.value) }
)

fun OrderItem.toEntity(orderId: UUID): OrderItemEntity = OrderItemEntity(
    id = null,
    productId = productId.value,
    productName = productName,
    quantity = quantity,
    price = price.amount,
    orderId = orderId
)
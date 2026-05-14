package com.example.ddd.application.service

import com.example.ddd.domain.model.*
import com.example.ddd.domain.port.OrderRepository
import java.util.UUID

class OrderService(private val orderRepository: OrderRepository) {

    fun createOrder(customerId: CustomerId): Order {
        val order = Order(
            id = OrderId(UUID.randomUUID()),
            customerId = customerId,
            items = emptyList(),
            status = OrderStatus.PENDING,
            createdAt = System.currentTimeMillis(),
            totalAmount = Money(0.0)
        )
        return orderRepository.save(order)
    }

    fun addItem(orderId: OrderId, productId: ProductId, productName: String, quantity: Int, price: Money): Order {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order not found: $orderId") }

        val item = OrderItem(productId, productName, quantity, price)
        val updatedOrder = order.addItem(item).calculateTotalAmount()
        return orderRepository.save(updatedOrder)
    }

    fun confirmOrder(orderId: OrderId): Order {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order not found: $orderId") }

        val confirmedOrder = order.confirm().calculateTotalAmount()
        return orderRepository.save(confirmedOrder)
    }

    fun cancelOrder(orderId: OrderId): Order {
        val order = orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order not found: $orderId") }

        val cancelledOrder = order.cancel()
        return orderRepository.save(cancelledOrder)
    }

    fun getOrder(orderId: OrderId): Order {
        return orderRepository.findById(orderId)
            .orElseThrow { IllegalArgumentException("Order not found: $orderId") }
    }

    fun getOrdersByCustomer(customerId: CustomerId): List<Order> {
        return orderRepository.findByCustomerId(customerId)
    }

    private fun Order.calculateTotalAmount(): Order {
        val total = this.items.sumOf { it.subtotal.amount }
        return this.copy(totalAmount = Money(total))
    }
}
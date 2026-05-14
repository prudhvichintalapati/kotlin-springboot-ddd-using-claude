package com.example.ddd.application.service

import com.example.ddd.domain.model.*
import com.example.ddd.domain.port.OrderRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.Optional
import java.util.UUID

class OrderServiceTest {

    private lateinit var orderService: OrderService
    private lateinit var mockRepository: MockOrderRepository

    @BeforeEach
    fun setup() {
        mockRepository = MockOrderRepository()
        orderService = OrderService(mockRepository)
    }

    @Test
    fun `createOrder creates a new pending order`() {
        val customerId = CustomerId(UUID.randomUUID())

        val order = orderService.createOrder(customerId)

        assert(order.status == OrderStatus.PENDING)
        assert(order.customerId == customerId)
        assert(order.items.isEmpty())
    }

    @Test
    fun `addItem adds item to existing order`() {
        val customerId = CustomerId(UUID.randomUUID())
        val order = orderService.createOrder(customerId)
        val productId = ProductId(UUID.randomUUID())

        val updatedOrder = orderService.addItem(
            order.id,
            productId,
            "Test Product",
            2,
            Money(10.0)
        )

        assert(updatedOrder.items.size == 1)
        assert(updatedOrder.items.first().productName == "Test Product")
    }

    @Test
    fun `confirmOrder changes status to CONFIRMED`() {
        val customerId = CustomerId(UUID.randomUUID())
        val order = orderService.createOrder(customerId)

        val confirmedOrder = orderService.confirmOrder(order.id)

        assert(confirmedOrder.status == OrderStatus.CONFIRMED)
    }

    @Test
    fun `cancelOrder changes status to CANCELLED`() {
        val customerId = CustomerId(UUID.randomUUID())
        val order = orderService.createOrder(customerId)

        val cancelledOrder = orderService.cancelOrder(order.id)

        assert(cancelledOrder.status == OrderStatus.CANCELLED)
    }

    @Test
    fun `getOrder throws exception when order not found`() {
        assertThrows<IllegalArgumentException> {
            orderService.getOrder(OrderId(UUID.randomUUID()))
        }
    }

    private class MockOrderRepository : OrderRepository {
        private val orders = mutableMapOf<OrderId, Order>()

        override fun save(order: Order): Order {
            orders[order.id] = order
            return order
        }

        override fun findById(id: OrderId): Optional<Order> {
            return Optional.ofNullable(orders[id])
        }

        override fun findByCustomerId(customerId: CustomerId): List<Order> {
            return orders.values.filter { it.customerId == customerId }
        }

        override fun delete(id: OrderId): Boolean {
            return orders.remove(id) != null
        }
    }
}
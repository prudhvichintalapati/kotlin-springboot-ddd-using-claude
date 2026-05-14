package com.example.ddd.infrastructure.adapter.api

import com.example.ddd.application.dto.*
import com.example.ddd.application.service.OrderService
import com.example.ddd.domain.model.CustomerId
import com.example.ddd.domain.model.Money
import com.example.ddd.domain.model.OrderId
import com.example.ddd.domain.model.ProductId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/orders")
class OrderController(private val orderService: OrderService) {

    @PostMapping
    fun createOrder(@RequestBody request: CreateOrderRequest): ResponseEntity<OrderResponse> {
        val order = orderService.createOrder(CustomerId(request.customerId))
        return ResponseEntity.status(HttpStatus.CREATED).body(order.toResponse())
    }

    @PostMapping("/{orderId}/items")
    fun addItem(
        @PathVariable orderId: UUID,
        @RequestBody request: AddItemRequest
    ): ResponseEntity<OrderResponse> {
        val order = orderService.addItem(
            OrderId(orderId),
            ProductId(request.productId),
            request.productName,
            request.quantity,
            Money(request.price)
        )
        return ResponseEntity.ok(order.toResponse())
    }

    @PostMapping("/{orderId}/confirm")
    fun confirmOrder(@PathVariable orderId: UUID): ResponseEntity<OrderResponse> {
        val order = orderService.confirmOrder(OrderId(orderId))
        return ResponseEntity.ok(order.toResponse())
    }

    @PostMapping("/{orderId}/cancel")
    fun cancelOrder(@PathVariable orderId: UUID): ResponseEntity<OrderResponse> {
        val order = orderService.cancelOrder(OrderId(orderId))
        return ResponseEntity.ok(order.toResponse())
    }

    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: UUID): ResponseEntity<OrderResponse> {
        val order = orderService.getOrder(OrderId(orderId))
        return ResponseEntity.ok(order.toResponse())
    }

    @GetMapping("/customer/{customerId}")
    fun getOrdersByCustomer(@PathVariable customerId: UUID): ResponseEntity<List<OrderResponse>> {
        val orders = orderService.getOrdersByCustomer(CustomerId(customerId))
        return ResponseEntity.ok(orders.map { it.toResponse() })
    }
}
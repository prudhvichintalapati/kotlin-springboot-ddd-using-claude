package com.example.ddd.infrastructure.config

import com.example.ddd.application.service.OrderService
import com.example.ddd.domain.port.OrderRepository
import com.example.ddd.infrastructure.adapter.persistence.OrderJpaRepository
import com.example.ddd.infrastructure.adapter.persistence.OrderRepositoryAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfig {

    @Bean
    fun orderRepository(jpaRepository: OrderJpaRepository): OrderRepository {
        return OrderRepositoryAdapter(jpaRepository)
    }

    @Bean
    fun orderService(orderRepository: OrderRepository): OrderService {
        return OrderService(orderRepository)
    }
}
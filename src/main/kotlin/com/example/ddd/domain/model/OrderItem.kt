package com.example.ddd.domain.model

data class OrderItem(
    val productId: ProductId,
    val productName: String,
    val quantity: Int,
    val price: Money
) {
    init {
        require(quantity > 0) { "Quantity must be positive" }
    }

    val subtotal: Money get() = Money(price.amount * quantity)
}

@JvmInline
value class Money(val amount: Double) {
    init {
        require(amount >= 0) { "Amount cannot be negative" }
    }

    operator fun plus(other: Money) = Money(this.amount + other.amount)
    operator fun times(factor: Int) = Money(this.amount * factor)
}
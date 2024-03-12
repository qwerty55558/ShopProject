package com.toyproject.shop.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Order {
    private Long orderId;
    private Long cartId;
    private Long totalPrice;
    private Long itemId;
    private Long amount;

    public Order(Long cartId, Long totalPrice, Long itemId, Long amount) {
        this.cartId = cartId;
        this.totalPrice = totalPrice;
        this.itemId = itemId;
        this.amount = amount;
    }

    public Order() {
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", cartId=" + cartId +
                ", totalPrice=" + totalPrice +
                ", itemId=" + itemId +
                ", amount=" + amount +
                '}';
    }
}

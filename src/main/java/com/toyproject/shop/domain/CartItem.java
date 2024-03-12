package com.toyproject.shop.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItem {
    private Long id;
    private Long cartId;
    private Long itemId;
    private Long count;

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", cartId=" + cartId +
                ", itemId=" + itemId +
                ", count=" + count +
                '}';
    }
}

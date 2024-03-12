package com.toyproject.shop.service.cart;

import com.toyproject.shop.domain.CartItem;
import com.toyproject.shop.repository.MemoryCartRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {
    @Test
    public void service(){

        CartService cartService = new CartService(new MemoryCartRepository());

        CartItem cartItem = cartService.addItem(1L, 1L, 5L);

        System.out.println("cartItem = " + cartItem);
    }
}
package com.toyproject.shop.service.cart;

import com.toyproject.shop.domain.Cart;
import com.toyproject.shop.domain.CartItem;
import com.toyproject.shop.domain.Item;
import com.toyproject.shop.repository.MemoryCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final MemoryCartRepository memorycartrepository;

    public CartItem addItem(Long memberId, Long itemId, Long count) {
        memorycartrepository.createCart(memberId);
        CartItem cartItem = new CartItem();
        cartItem.setItemId(itemId);
        cartItem.setCartId(memberId);
        cartItem.setCount(count);
        return memorycartrepository.addCart(cartItem);
    }
}

package com.toyproject.shop.repository;

import com.toyproject.shop.domain.Cart;
import com.toyproject.shop.domain.CartItem;
import com.toyproject.shop.domain.Item;
import com.toyproject.shop.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class MemoryCartRepository {
    private static Map<Long, Cart> store = new ConcurrentHashMap<>();
    private static Map<Long, CartItem> itemStore = new ConcurrentHashMap<>();
    private static AtomicLong count = new AtomicLong();

    public Cart createCart(Long memberId) {
        if (store.get(memberId) != null) {
            Cart cart = new Cart();
            store.put(memberId, cart);
            return cart;
        }
        return store.get(memberId);
    }

    public CartItem addCart(CartItem cartItem) {
        Optional<CartItem> item = findByMember(cartItem.getCartId()).stream()
                .filter(c -> c.getItemId().equals(cartItem.getItemId()))
                .findAny();
        if (item.isPresent()) {
            CartItem cartItem1 = item.get();
            cartItem1.setCount(cartItem1.getCount() + cartItem.getCount());
            itemStore.replace(item.get().getId(), cartItem1);
        }else {
            cartItem.setId(count.incrementAndGet());
            itemStore.put(cartItem.getId(), cartItem);
        }
        return cartItem;
    }

    public void deleteCart(Long Id, Long itemId) {
        List<CartItem> byMember = findByMember(Id);
        for (CartItem cartItem : byMember) {
            if (cartItem.getItemId().equals(itemId)) {
                itemStore.remove(cartItem.getId());
            }
        }
    }

    public CartItem findById(Long id) {
        return itemStore.get(id);
    }

    public List<CartItem> findByMember(Long memberId){
        return itemStore.values().stream()
                .filter(c -> c.getCartId().equals(memberId))
                .toList();
    }





}

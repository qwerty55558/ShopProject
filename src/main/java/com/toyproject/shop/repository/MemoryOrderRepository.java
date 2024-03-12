package com.toyproject.shop.repository;

import com.toyproject.shop.domain.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Repository
@Slf4j
public class MemoryOrderRepository {
    Map<Long, Order> store = new ConcurrentHashMap<>();
    AtomicLong sequence = new AtomicLong();
    public Order save(Order order){
        order.setOrderId(sequence.incrementAndGet());
        store.put(sequence.get(), order);
        return order;
    }

    public Collection<Order> findAll(){
        return store.values();
    }

    public Collection<Order> findByMember(Long memberId) {
        return store.values().stream().filter(c -> Objects.equals(c.getCartId(), memberId)).toList();
    }
}

package com.toyproject.shop.service.test;

import com.toyproject.shop.domain.Item;
import com.toyproject.shop.domain.Member;
import com.toyproject.shop.repository.MemoryItemRepository;
import com.toyproject.shop.repository.MemoryMemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataInit {
    private final MemoryMemberRepository memberRepository;
    private final MemoryItemRepository memoryItemRepository;

    @PostConstruct
    public void init(){
        Item item = new Item();
        item.setItemName("테스트상품");
        item.setPrice(1000);
        item.setItemSrc("/assets/testItem.jpg");
        item.setStarCount(4);
        item.setSale(true);
        item.setSalePercentage(10);

        Item item2 = new Item();
        item2.setItemName("테스트상품2");
        item2.setPrice(10000);
        item2.setItemSrc("/assets/testItem2.jpg");
        item2.setStarCount(3);
        item2.setSale(false);
        item2.setSalePercentage(50);

        Item item3 = new Item();
        item3.setItemName("테스트상품3");
        item3.setPrice(120000);
        item3.setItemSrc("/assets/testItem3.jpg");
        item3.setStarCount(2);
        item3.setSale(true);
        item3.setSalePercentage(32);

        Item item4 = new Item();
        item4.setItemName("테스트상품4");
        item4.setPrice(900000);
        item4.setItemSrc("/assets/testItem4.jpg");
        item4.setStarCount(5);
        item4.setSale(false);
        item4.setSalePercentage(90);

        Item item5 = new Item();
        item5.setItemName("테스트상품4");
        item5.setPrice(900000);
        item5.setItemSrc("/assets/testItem4.jpg");
        item5.setStarCount(5);
        item5.setSale(false);
        item5.setSalePercentage(90);


        memoryItemRepository.save(item);
        memoryItemRepository.save(item2);
        memoryItemRepository.save(item3);
        memoryItemRepository.save(item4);
        memoryItemRepository.save(item5);


        Member member = new Member();
        member.setLoginId("test");
        member.setName("test");
        member.setPassword("1234");
        memberRepository.save(member);

        Member member2 = new Member();
        member2.setLoginId("test2");
        member2.setName("test2");
        member2.setPassword("1234");
        memberRepository.save(member2);
    }
}

package com.toyproject.shop.controller;

import com.toyproject.shop.domain.DiscountCode;
import com.toyproject.shop.domain.Order;
import com.toyproject.shop.repository.MemoryItemRepository;
import com.toyproject.shop.repository.MemoryOrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping
public class OrderController {

    private final MemoryItemRepository memoryItemRepository;
    private final MemoryOrderRepository memoryOrderRepository;

    @GetMapping("/order")
    public String showOrder(@ModelAttribute Order order, Model model) {
        return "redirect:/members/orderHistoryMember";
    }

    @PostMapping("/order")
    public String addOrder(HttpServletRequest req,
             Model model) {
        String[] itemIds = req.getParameterValues("itemId");
        String[] counts = req.getParameterValues("count");
        String[] cartIds = req.getParameterValues("cartId");
        String[] saleCodes = req.getParameterValues("saleCode");
        List<Long> totalPrice = new ArrayList<>();
        Long price = 0L;
        for (int i = 0; i < counts.length; i++) {
            if (memoryItemRepository.findById(Long.valueOf(itemIds[i])).isSale()){
                price = (long) (memoryItemRepository.findById(Long.valueOf(itemIds[i])).getPrice() - (memoryItemRepository.findById(Long.valueOf(itemIds[i])).getPrice()
                                        * (memoryItemRepository.findById(Long.valueOf(itemIds[i])).getSalePercentage() * 0.01)));
            }else {
                price = Long.valueOf(memoryItemRepository.findById(Long.valueOf(itemIds[i])).getPrice());
            }

            Optional<String> any = Arrays.stream(saleCodes).findAny();
            if (any.isPresent()) {
                price = checkDiscountCode(price, any.get());
            }
            totalPrice.add(price * Long.parseLong(counts[i]));

            memoryOrderRepository.save(new Order(Long.parseLong(cartIds[i]), totalPrice.get(i),
                    Long.parseLong(itemIds[i]),Long.parseLong(counts[i])));
        }

        model.addAttribute("orders", memoryOrderRepository.findByMember(Long.parseLong(cartIds[0])));
        return "/members/orderHistoryMember";
    }

    private Long checkDiscountCode(Long price, String checkCode) {
        for (String code : DiscountCode.codes) {
            if (checkCode.equals(code) && checkCode.equals("10percent")) {
                return (long) (price - (price * 0.1));
            }
            if (code.equals(checkCode) && checkCode.equals("500coin")) {
                return (price - 500);
            }
        }
        return price;
    }

}

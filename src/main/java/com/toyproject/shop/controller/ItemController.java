package com.toyproject.shop.controller;

import com.toyproject.shop.domain.*;
import com.toyproject.shop.repository.MemoryCartRepository;
import com.toyproject.shop.repository.MemoryItemRepository;
import com.toyproject.shop.service.cart.CartService;
import com.toyproject.shop.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/item")
public class ItemController {

    private final CartService cartService;
    private final MemoryCartRepository memoryCartRepository;
    private final MemoryItemRepository memoryItemRepository;

    @GetMapping("cart")
    public String cart(Model model, HttpServletRequest req) {
        HttpSession session = req.getSession();
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        model.addAttribute("itemList", memoryCartRepository.findByMember(loginMember.getID()));
        model.addAttribute("itemInfo", memoryItemRepository.findAll());
        model.addAttribute("discountCode", new DiscountCode());
        return "item/cart";
    }

    @GetMapping("cart/add/{id}")
    public String cartAdd(@PathVariable Long id, HttpServletRequest req) {
        HttpSession session = req.getSession();
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        cartService.addItem(loginMember.getID(), id, 1L);
        log.info("addcart = {}", memoryCartRepository.findByMember(loginMember.getID()));
        return "redirect:/home";
    }

    @PostMapping("cart/delete")
    public String cartdelete(HttpServletRequest req) {
        String[] parameterValues = req.getParameterValues("itemIdDelete");
        HttpSession session = req.getSession();
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        for (String parameterValue : parameterValues) {
            log.info("parameterVlaue = {}",parameterValue);
            memoryCartRepository.deleteCart(loginMember.getID(), Long.valueOf(parameterValue));
        }
//        memoryCartRepository.deleteCart(item.getId());
//        log.info("item = {}",item.getId());
        return "redirect:/item/cart";
    }
}

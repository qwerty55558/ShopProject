package com.toyproject.shop.controller;

import com.toyproject.shop.domain.Item;
import com.toyproject.shop.domain.Member;
import com.toyproject.shop.repository.MemoryCartRepository;
import com.toyproject.shop.repository.MemoryItemRepository;
import com.toyproject.shop.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemoryItemRepository memoryItemRepository;
    private final MemoryCartRepository memoryCartRepository;

    @GetMapping({"/", "/home"})
    public String welcomehome(@SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false)Member loginMember, Model model) {
        // 상품 리스트
        List<Item> items = memoryItemRepository.findAll();
        model.addAttribute("items", items);

        // 세션 로그인 처리
//        HttpSession session = req.getSession(false);
//        if (session == null) {
//            return "home/home";
//        }

        if (loginMember == null) {
            return "home/home";
        }
        
        long count = memoryCartRepository.findByMember(loginMember.getID()).size();
        model.addAttribute("member", loginMember);
        model.addAttribute("cartNum", count);
        return "home/loginHome";




    }

    @GetMapping("/happystore")
    public String aboutstore(){
        return "home/about";
    }

}

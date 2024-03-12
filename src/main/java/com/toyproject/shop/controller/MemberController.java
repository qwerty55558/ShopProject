package com.toyproject.shop.controller;

import com.toyproject.shop.domain.Member;
import com.toyproject.shop.repository.MemoryMemberRepository;
import com.toyproject.shop.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Enumeration;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemoryMemberRepository memberRepository;

    @GetMapping("/add")
    public String addMemberView(@ModelAttribute("member") Member member) {
        return "members/addMember";
    }

    @PostMapping("/add")
    public String save(@Validated @ModelAttribute Member member, BindingResult result) {
        if (result.hasErrors()) {
            return "members/addMember";
        }
        memberRepository.save(member);
        return "redirect:/home";
    }

    @GetMapping("/findPw")
    public String findPassword() {
        return "members/findPasswordMember";
    }

    @PostMapping("/info")
    public String Information(HttpServletRequest req, Model model){
        HttpSession session = req.getSession(false);
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        model.addAttribute("member", loginMember);
        return "members/informationMember";
    }
}

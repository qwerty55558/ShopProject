package com.toyproject.shop.controller;

import com.toyproject.shop.domain.LoginForm;
import com.toyproject.shop.domain.Member;
import com.toyproject.shop.service.login.LoginService;
import com.toyproject.shop.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form) {
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("loginForm") LoginForm form, @RequestParam(name = "redirectURL", required = false) String redirectURL, BindingResult bindingResult, HttpServletRequest req) {

        if (bindingResult.hasErrors()) {
            log.info("errors = {}", bindingResult.getAllErrors());
            return "login/loginForm";
        }

        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        log.info("login = {}", loginMember);
        // RememberMe 구현을 위해 remember 세션 처리 TODO
        log.info("isRemember = {}", form.isRemember());

        if (loginMember == null) {
            bindingResult.reject("check.login.IdorPw", "check");
            return "login/loginForm";
        }

        // 로그인 성공 처리 TODO
        HttpSession session = req.getSession(true);
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        if (redirectURL != null) {
            System.out.println("redirectURL = " + redirectURL);
            return "redirect:" + redirectURL;
        }
        return "redirect:/home";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest req) {

        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}

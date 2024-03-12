package com.toyproject.shop.service.login;

import com.toyproject.shop.domain.Member;
import com.toyproject.shop.repository.MemoryMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemoryMemberRepository memoryMemberRepository;
    /**
     * @return null이면 Login Fail
     */
    public Member login(String loginId, String password) {
//        return memoryMemberRepository.findByLoginId(loginId)
//                .filter(m -> m.getPassword().equals(password))
//                .orElse(null);
        Optional<Member> byLoginId = memoryMemberRepository.findByLoginId(loginId);
        if (byLoginId.isPresent()) {
            Member member = byLoginId.get();
            if (member.getPassword().equals(password)) {
                return member;
            }
        } else {
            return null;
        }
        return null;
    }


}

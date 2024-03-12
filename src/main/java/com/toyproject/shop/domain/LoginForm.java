package com.toyproject.shop.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginForm {
    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
    private boolean Remember;
}

package com.toyproject.shop.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DiscountCode {
    public static List<String> codes = List.of("10percent","500coin");
}

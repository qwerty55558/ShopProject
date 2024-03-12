package com.toyproject.shop.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {

    private Long ItemId;
    private String ItemName;
    private Integer Price;
    private String ItemSrc;
    private Integer StarCount;
    private boolean Sale;
    private Integer SalePercentage;


    public Item(){
    }

    @Override
    public String toString() {
        return "Item{" +
                "ItemId=" + ItemId +
                ", ItemName='" + ItemName + '\'' +
                ", Price=" + Price +
                ", ItemSrc='" + ItemSrc + '\'' +
                ", StarCount=" + StarCount +
                ", Sale=" + Sale +
                ", SalePercentage=" + SalePercentage +
                '}';
    }
}

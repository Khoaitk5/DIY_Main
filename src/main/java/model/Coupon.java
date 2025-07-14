/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;

/**
 *
 * @author HP
 */
public class Coupon {
    private String code;
    private String description;
    private double discountValue;
    private boolean isPercent;
    private double minOrderValue;
    private int quantity;
    private LocalDateTime expiredAt;
    private int maxUsagePerUser;

    public Coupon(String code, String description, double discountValue, boolean isPercent, double minOrderValue, int quantity, LocalDateTime expiredAt, int maxUsagePerUser) {
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.isPercent = isPercent;
        this.minOrderValue = minOrderValue;
        this.quantity = quantity;
        this.expiredAt = expiredAt;
        this.maxUsagePerUser = maxUsagePerUser;
    }


    public Coupon() {
    }
    

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public boolean isIsPercent() {
        return isPercent;
    }

    public void setIsPercent(boolean isPercent) {
        this.isPercent = isPercent;
    }

    public double getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(double minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public int getMaxUsagePerUser() {
        return maxUsagePerUser;
    }

    public void setMaxUsagePerUser(int maxUsagePerUser) {
        this.maxUsagePerUser = maxUsagePerUser;
    }

    

    
        
    @Override
    public String toString() {
        return "Coupon{" + "code=" + code + ", description=" + description + ", discountValue=" + discountValue + ", isPercent=" + isPercent + ", minOrderValue=" + minOrderValue + ", quantity=" + quantity + ", expiredAt=" + expiredAt + '}';
    }
    
    
}

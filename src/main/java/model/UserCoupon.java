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
public class UserCoupon {

    private int userId;
    private String couponCode;
    private int usageCount;
    private LocalDateTime usedAt;

    public UserCoupon(int userId, String couponCode, int usageCount, LocalDateTime usedAt) {
        this.userId = userId;
        this.couponCode = couponCode;
        this.usageCount = usageCount;
        this.usedAt = usedAt;
    }

    public UserCoupon() {
    }
    
    

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
    
    
}

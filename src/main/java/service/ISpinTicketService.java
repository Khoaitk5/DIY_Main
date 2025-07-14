/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;

import model.SpinTicket;

/**
 *
 * @author HP
 */
public interface ISpinTicketService {
     /**
     * Lấy ticket của user
     */
    SpinTicket getTicketByUserId(int userId);

    /**
     * Kiểm tra user có được phép quay không (cách 7 ngày)
     */
    boolean canSpin(int userId);

    /**
     * Cập nhật hoặc thêm mới playDate sau khi quay
     */
    void updatePlayDate(int userId);

    /**
     * Tăng hoặc giảm ticket_number nếu cần
     */
    void updateTicketNumber(int userId, int newValue);
}

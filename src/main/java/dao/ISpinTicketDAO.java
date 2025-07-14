/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import model.SpinTicket;

/**
 *
 * @author HP
 */
public interface ISpinTicketDAO {
    SpinTicket findByUserId(int userId);
    void insert(SpinTicket ticket);
    void update(SpinTicket ticket);
    boolean canSpin(int userId);
}

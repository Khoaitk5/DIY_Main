/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.SpinTicketDao;
import java.time.LocalDateTime;
import model.SpinTicket;

/**
 *
 * @author HP
 */
public class SpinTicketService implements ISpinTicketService{
    private final SpinTicketDao spinTicketDao = new SpinTicketDao();

    @Override
    public SpinTicket getTicketByUserId(int userId) {
        return spinTicketDao.findByUserId(userId);
    }

    @Override
    public boolean canSpin(int userId) {
        return spinTicketDao.canSpin(userId);
    }

    @Override
    public void updatePlayDate(int userId) {
        SpinTicket ticket = spinTicketDao.findByUserId(userId);
        if (ticket == null) {
            ticket = new SpinTicket(userId);
            ticket.setPlayDate(LocalDateTime.now());
            spinTicketDao.insert(ticket);
        } else {
            ticket.setPlayDate(LocalDateTime.now());
            spinTicketDao.update(ticket);
        }
    }

    @Override
    public void updateTicketNumber(int userId, int newValue) {
        SpinTicket ticket = spinTicketDao.findByUserId(userId);
        if (ticket != null) {
            ticket.setTicketNumber(newValue);
            spinTicketDao.update(ticket);
        }
    }
}

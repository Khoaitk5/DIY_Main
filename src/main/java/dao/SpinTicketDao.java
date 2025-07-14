/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import model.SpinTicket;

/**
 *
 * @author HP
 */
public class SpinTicketDao implements ISpinTicketDAO{
     @Override
    public SpinTicket findByUserId(int userId) {
        String sql = "SELECT * FROM spin_ticket WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                SpinTicket ticket = new SpinTicket();
                ticket.setId(rs.getInt("id"));
                ticket.setUserId(rs.getInt("user_id"));
                ticket.setTicketNumber(rs.getInt("ticket_number"));

                Timestamp ts = rs.getTimestamp("play_date");
                if (ts != null) {
                    ticket.setPlayDate(ts.toLocalDateTime());
                }

                return ticket;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert(SpinTicket ticket) {
        String sql = "INSERT INTO spin_ticket (user_id, ticket_number, play_date) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticket.getUserId());
            stmt.setInt(2, ticket.getTicketNumber());

            if (ticket.getPlayDate() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(ticket.getPlayDate()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(SpinTicket ticket) {
        String sql = "UPDATE spin_ticket SET ticket_number = ?, play_date = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ticket.getTicketNumber());

            if (ticket.getPlayDate() != null) {
                stmt.setTimestamp(2, Timestamp.valueOf(ticket.getPlayDate()));
            } else {
                stmt.setNull(2, Types.TIMESTAMP);
            }

            stmt.setInt(3, ticket.getUserId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canSpin(int userId) {
        SpinTicket ticket = findByUserId(userId);
        if (ticket == null || ticket.getPlayDate() == null) {
            return true; // chưa có lượt quay hoặc chưa từng quay
        }

        long days = ChronoUnit.DAYS.between(ticket.getPlayDate().toLocalDate(), LocalDateTime.now().toLocalDate());
        return days >= 7;
    }
}

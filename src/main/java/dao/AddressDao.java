/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import model.Address;

/**
 *
 * @author HP
 */
public class AddressDao {

    private static final String INSERT_ADDRESS = "INSERT INTO Address (street, district, city) VALUES (?, ?, ?)";
    private static final String SELECT_ADDRESS_BY_ID = "SELECT * FROM Address WHERE id = ?";
    private static final String UPDATE_ADDRESS = "UPDATE Address SET street = ?, district = ?, city = ? WHERE id = ?";
    private static final String DELETE_ADDRESS = "DELETE FROM Address WHERE id = ?";
    private static final String SELECT_EXISTING_ADDRESS = "SELECT id FROM Address WHERE street = ? AND district = ? AND city = ?";

    public static Integer findExistingAddressId(Address address) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_EXISTING_ADDRESS)) {
            pstmt.setString(1, address.getStreet());
            pstmt.setString(2, address.getDistrict());
            pstmt.setString(3, address.getCity());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int insertAddress(Address address) {
        Integer existingId = findExistingAddressId(address);
        if (existingId != null) {
            return existingId;
        }

        int addressId = -1;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(INSERT_ADDRESS, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, address.getStreet());
            pstmt.setString(2, address.getDistrict());
            pstmt.setString(3, address.getCity());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    addressId = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addressId;
    }

    public static Address selectAddress(int id) {
        Address address = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_ADDRESS_BY_ID)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String street = rs.getString("street");
                String district = rs.getString("district");
                String city = rs.getString("city");

                address = new Address(id, street, district, city);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

    public static boolean updateAddress(Address address) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(UPDATE_ADDRESS)) {

            pstmt.setString(1, address.getStreet());
            pstmt.setString(2, address.getDistrict());
            pstmt.setString(3, address.getCity());
            pstmt.setInt(4, address.getId());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public static boolean deleteAddressById(int id) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(DELETE_ADDRESS)) {

            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteUserAddressLink(int userId, int addressId) {
        String sql = "DELETE FROM User_Address WHERE user_id = ? AND address_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, addressId);
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}

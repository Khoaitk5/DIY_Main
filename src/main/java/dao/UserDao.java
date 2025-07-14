package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Address;
import model.User;

public class UserDao implements IUserDAO {

    private static final String LOGIN = "SELECT id FROM Users WHERE username = ? AND password = ?";
    private static final String LOGIN1 = "SELECT * FROM Users WHERE username = ?";
    private static final String INSERT_USER = "INSERT INTO Users (username, name, email, password, role, status) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM Users WHERE id = ?";
    private static final String UPDATE_USER = "UPDATE Users SET username = ?, name = ?, email = ?, role = ?, status = ?, password = ? WHERE id = ?";
    private static final String DELETE_USER = "UPDATE Users SET status = ? WHERE id = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM Users";
    private static final String SELECT_BY_NAME = "SELECT * FROM Users WHERE username = ?";

    private static final AddressDao addressDao = new AddressDao();
    private static final String SELECT_USER_ADDRESSES = "SELECT a.* FROM Address a JOIN User_Address ua ON a.id = ua.address_id WHERE ua.user_id = ?";
    private static final String INSERT_USER_ADDRESS = "INSERT INTO User_Address (user_id, address_id) VALUES (?, ?)";

    public static boolean checkLogin(String username, String password) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(LOGIN)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static User checkLogin1(String username, String password) {
        User user = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(LOGIN1)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (storedPassword.equals(password)) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String role = rs.getString("role");
                    String email = rs.getString("email");
                    boolean status = rs.getBoolean("status");
                    List<Address> addresses = getAddressesByUserId(id);
                    user = new User(id, username, name, email, role, status, addresses, storedPassword);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public boolean insertUser(User user) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPassword());
            pstmt.setString(5, user.getRole());
            pstmt.setBoolean(6, user.isStatus());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    for (Address addr : user.getAddress()) {
                        int addrId = addressDao.insertAddress(addr);
                        linkUserToAddress(userId, addrId);
                    }
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User selectUser(int id) {
        User user = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_USER_BY_ID)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                String name = rs.getString("name");
                String role = rs.getString("role");
                String email = rs.getString("email");
                boolean status = rs.getBoolean("status");
                String password = rs.getString("password");
                List<Address> addresses = getAddressesByUserId(id);
                user = new User(id, username, name, email, role, status, addresses, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public List<User> selectAllUsers() {
        List<User> userList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_ALL_USERS)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String name = rs.getString("name");
                String role = rs.getString("role");
                String email = rs.getString("email");
                boolean status = rs.getBoolean("status");
                String password = rs.getString("password");
                List<Address> addresses = getAddressesByUserId(id);
                userList.add(new User(id, username, name, email, role, status, addresses, password));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    @Override
    public boolean deleteUser(int id) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(DELETE_USER)) {
            pstmt.setBoolean(1, false);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(UPDATE_USER)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole());
            pstmt.setBoolean(5, user.isStatus());
            pstmt.setString(6, user.getPassword());
            pstmt.setInt(7, user.getId());
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public User findByUsername(String username) {
        User user = null;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_NAME)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String role = rs.getString("role");
                boolean status = rs.getBoolean("status");
                String password = rs.getString("password");
                List<Address> addresses = getAddressesByUserId(id);
                user = new User(id, username, name, email, role, status, addresses, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public Integer saveOrUpdateOAuthUser(String email, String name) {
        String username = email.split("@")[0];
        String role = "user";
        boolean status = true;
        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT id FROM Users WHERE email = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, email);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String updateSql = "UPDATE Users SET name = ? WHERE id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, name);
                        updateStmt.setInt(2, userId);
                        updateStmt.executeUpdate();
                    }
                    return userId;
                } else {
                    String insertSql = "INSERT INTO Users (username, name, email, password, role, status) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                        insertStmt.setString(1, username);
                        insertStmt.setString(2, name);
                        insertStmt.setString(3, email);
                        insertStmt.setString(4, null);
                        insertStmt.setString(5, role);
                        insertStmt.setBoolean(6, status);
                        insertStmt.executeUpdate();
                        try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                return generatedKeys.getInt(1);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Address> getAddressesByUserId(int userId) throws SQLException {
        List<Address> addresses = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(SELECT_USER_ADDRESSES)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Address addr = new Address();
                addr.setId(rs.getInt("id"));
                addr.setStreet(rs.getString("street"));
                addr.setDistrict(rs.getString("district"));
                addr.setCity(rs.getString("city"));
                addresses.add(addr);
            }
        }
        return addresses;
    }

    public static void linkUserToAddress(int userId, int addressId) throws SQLException {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(INSERT_USER_ADDRESS)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, addressId);
            pstmt.executeUpdate();
        }
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

    public User findByEmail(String email) {
        User user = null;
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String name = rs.getString("name");
                String role = rs.getString("role");
                boolean status = rs.getBoolean("status");
                String password = rs.getString("password");
                List<Address> addresses = getAddressesByUserId(id);
                user = new User(id, username, name, email, role, status, addresses, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

}

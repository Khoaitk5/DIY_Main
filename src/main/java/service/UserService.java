package service;

import dao.UserDao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Address;
import model.User;

public class UserService implements IUserService {

    private final UserDao userDao = new UserDao();

    @Override
    public boolean register(String username, String password, String email, Address address) throws SQLException {
        if (isUsernameTaken(username)) {
            return false; // Username already exists
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        List<Address> addresses = new ArrayList<>();
        addresses.add(address);
        user.setAddress(addresses);
        user.setRole("user");
        user.setStatus(true);
        return userDao.insertUser(user);
    }

    @Override
    public User login(String username, String password) throws SQLException {
        return UserDao.checkLogin1(username, password); // null if invalid
    }

    public boolean checkLogin(String username, String password) {
        return UserDao.checkLogin(username, password);
    }

    @Override
    public User getUserById(int id) throws SQLException {
        return userDao.selectUser(id);
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        return userDao.updateUser(user);
    }

    @Override
    public boolean isUsernameTaken(String username) throws SQLException {
        return userDao.findByUsername(username) != null;
    }

    public List<Address> getAddressesByUserId(int userId) throws SQLException {
        return userDao.getAddressesByUserId(userId);
    }

    public void linkUserToAddress(int userId, int addressId) throws SQLException {
        userDao.linkUserToAddress(userId, addressId);
    }

    public boolean deleteUserAddressLink(int userId, int addressId) {
        return UserDao.deleteUserAddressLink(userId, addressId);
    }

    public boolean isEmailTaken(String email) throws SQLException {
        return userDao.findByEmail(email) != null;
    }

}

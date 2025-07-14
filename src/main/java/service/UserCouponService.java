package service;

import dao.UserCouponDao;
import java.util.List;
import model.UserCoupon;

public class UserCouponService implements IUserCouponService {

    private final UserCouponDao userCouponDao;

    public UserCouponService() {
        this.userCouponDao = new UserCouponDao();
    }

    @Override
    public UserCoupon getByUserAndCoupon(int userId, String couponCode) {
        return userCouponDao.findByUserAndCoupon(userId, couponCode);
    }

    @Override
    public boolean canUserUse(int userId, String couponCode) {
        return userCouponDao.canUseCoupon(userId, couponCode);
    }

    @Override
    public List<UserCoupon> getUserCoupons(int userId) {
        return userCouponDao.getCouponsOfUser(userId);
    }

    @Override
    public void assignCoupon(int userId, String couponCode) {
        userCouponDao.assignCouponToUser(userId, couponCode);
    }

    @Override
    public void increaseUsage(int userId, String couponCode) {
        userCouponDao.increaseUsage(userId, couponCode);
    }
}

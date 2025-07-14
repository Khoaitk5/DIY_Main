package service;

import dao.CouponDao;
import model.Coupon;
import java.util.List;

public class CouponService implements ICouponService {

    private final CouponDao couponDao;

    public CouponService() {
        this.couponDao = new CouponDao();
    }

    @Override
    public Coupon findByCode(String code) {
        return couponDao.findByCode(code);
    }

    @Override
    public boolean isValid(Coupon coupon) {
        if (coupon == null) return false;
        return couponDao.isValid(coupon.getCode());
    }

    @Override
    public void decreaseQuantity(String code) {
        couponDao.decreaseQuantity(code);
    }

    @Override
    public List<Coupon> findAllAvailable() {
        return couponDao.findAllAvailable();
    }

    @Override
    public void insert(Coupon coupon) {
        couponDao.insert(coupon);
    }

    @Override
    public void update(Coupon coupon) {
        couponDao.update(coupon);
    }

    @Override
    public void delete(String code) {
        couponDao.delete(code);
    }
}


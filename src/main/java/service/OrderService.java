package service;

import dao.OrderDao;
import dao.OrderItemDao;
import java.util.List;
import java.sql.SQLException;

import model.Order;
import model.OrderItem;
import model.OrderStatus;

public class OrderService implements IOrderService {

    private final OrderDao orderDao;
    private final OrderItemDao orderItemDao;

    public OrderService() {
        this.orderDao = new OrderDao();
        this.orderItemDao = new OrderItemDao();
    }

    @Override
    public int createOrder(Order order, List<OrderItem> items) throws SQLException {
        int orderId = orderDao.insertOrder(order);
        if (orderId > 0) {
            for (OrderItem item : items) {
                item.setOrderId(orderId); // gán orderId mới cho từng OrderItem
            }
            orderItemDao.insertOrderItems(items);
        }
        return orderId;
    }

    @Override
    public Order getOrderById(int orderId) throws SQLException {
        Order order = orderDao.getOrderById(orderId);
        if (order != null) {
            List<OrderItem> items = orderItemDao.getOrderItemsByOrderId(orderId);
            order.setItems(items);
        }
        return order;
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) throws SQLException {
        List<Order> orders = orderDao.getOrdersByUserId(userId);
        for (Order order : orders) {
            List<OrderItem> items = orderItemDao.getOrderItemsByOrderId(order.getId());
            order.setItems(items);
        }
        return orders;
    }

    @Override
    public boolean updateOrderStatus(int orderId, OrderStatus status) throws SQLException {
        return orderDao.updateOrderStatus(orderId, status.name());
    }

    @Override
    public boolean cancelOrder(int orderId) throws SQLException {
        return orderDao.cancelOrder(orderId);
    }

    @Override
    public boolean updateShippingAddress(int orderId, int newAddressId) throws SQLException {
        return orderDao.updateAddress(orderId, newAddressId);
    }

    @Override
    public double getTotalSpentByUser(int userId) throws SQLException {
        return orderDao.getTotalSpentByUser(userId);
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(int orderId) throws SQLException {
        return orderItemDao.getOrderItemsByOrderId(orderId);
    }

    @Override
    public double calculateOrderTotal(int orderId) throws SQLException {
        return orderItemDao.calculateOrderTotal(orderId);
    }
}

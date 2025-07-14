/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import dao.AddressDao;
import model.Address;

/**
 *
 * @author HP
 */
public class AddressService implements IAddressService{
    AddressDao addressDao = new AddressDao();
    
    @Override
    public int addAddress(Address address) {
        return addressDao.insertAddress(address);
    }

    @Override
    public Address getAddressById(int id) {
        return addressDao.selectAddress(id);
    }

    @Override
    public boolean updateAddress(Address address) {
        return addressDao.updateAddress(address);
    }
    
    public boolean deleteAddressById(int id) {
        return AddressDao.deleteAddressById(id);
    }
}

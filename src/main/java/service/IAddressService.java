/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package service;

import model.Address;

/**
 *
 * @author HP
 */
public interface IAddressService {
    
    int addAddress(Address address);
    
    Address getAddressById(int id);
    
    boolean updateAddress(Address address);
}

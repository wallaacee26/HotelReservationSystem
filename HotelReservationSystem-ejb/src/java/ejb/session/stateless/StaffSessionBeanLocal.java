/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Staff;
import javax.ejb.Local;
import util.exception.StaffDNEException;
import util.exception.StaffUsernameExistsException;

/**
 *
 * @author yewkhang
 */
@Local
public interface StaffSessionBeanLocal {

    public Staff retrieveStaffByUsername(String username) throws StaffDNEException;

    public Long createNewStaff(Staff staff) throws StaffUsernameExistsException;
    
}

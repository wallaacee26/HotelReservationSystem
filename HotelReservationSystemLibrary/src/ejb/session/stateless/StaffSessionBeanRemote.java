/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Staff;
import javax.ejb.Remote;
import util.exception.InvalidLoginCredentialException;
import util.exception.StaffDNEException;
import util.exception.StaffUsernameExistsException;

/**
 *
 * @author yewkhang
 */
@Remote
public interface StaffSessionBeanRemote {
    public Staff retrieveStaffByUsername(String username) throws StaffDNEException;
    public Long createNewStaff(Staff staff) throws StaffUsernameExistsException;

    public Staff staffLogin(String username, String password) throws InvalidLoginCredentialException;
}

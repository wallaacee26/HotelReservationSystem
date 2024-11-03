/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.StaffSessionBeanLocal;
import entity.Staff;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.enumeration.AccessRightEnum;
import util.exception.StaffDNEException;
import util.exception.StaffUsernameExistsException;

/**
 *
 * @author yewkhang
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {
    @EJB
    private StaffSessionBeanLocal staffSBLocal;

    public DataInitSessionBean() {
    }
    
    @PostConstruct
    public void postConstruct() {
        try {
            staffSBLocal.retrieveStaffByUsername("test admin");
        } catch (StaffDNEException ex) {
            loadTestData();
        }
    }
    
    private void loadTestData() {
        try {
            staffSBLocal.createNewStaff(new Staff("test admin", "123", AccessRightEnum.ADMINISTRATOR));
        } catch (StaffUsernameExistsException ex) {
            ex.printStackTrace();
        }
    }
}

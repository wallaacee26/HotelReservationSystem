/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Staff;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;
import util.exception.StaffDNEException;
import util.exception.StaffUsernameExistsException;

/**
 *
 * @author yewkhang
 */
@Stateless
public class StaffSessionBean implements StaffSessionBeanRemote, StaffSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public StaffSessionBean() {
    }

    @Override
    public Long createNewStaff(Staff staff) throws StaffUsernameExistsException {
        try {
            em.persist(staff);
            em.flush();
            return staff.getStaffId();
        } catch(PersistenceException ex) {
           if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new StaffUsernameExistsException();
                } else {
                    throw new StaffUsernameExistsException(ex.getMessage()); // UnknownPersistenceException, but just using this exception for simplicity
                }
            } else {
               throw new StaffUsernameExistsException(ex.getMessage()); // UnknownPersistenceException, but just using this exception for simplicity
           }
        }
    }
    
    @Override
    public Staff retrieveStaffByUsername(String username) throws StaffDNEException {
        Query query = em.createQuery("SELECT s FROM Staff s WHERE s.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try {
            return (Staff)query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new StaffDNEException("Staff username " + username + " does not exist!");
        }
    }
    
    @Override
    public List<Staff> retrieveAllStaffs() {
        Query query = em.createQuery("SELECT s from Staff s");
        return query.getResultList();
    }
    
    @Override
    public Staff staffLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            Staff s = retrieveStaffByUsername(username);
            
            if (s.getPassword().equals(password)) {
                // preload any lazy data if needed
                s.getStaffId();
                return s;
            } else {
                throw new InvalidLoginCredentialException("Staff password is incorrect!");
            }
        } catch (StaffDNEException ex) {
            throw new InvalidLoginCredentialException("Staff username does not exist!");
        }
    }
}

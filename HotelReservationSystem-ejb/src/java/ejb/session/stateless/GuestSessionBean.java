/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.GuestDNEException;
import util.exception.GuestExistsException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author wallace
 */
@Stateless
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewGuest(Guest guest) throws GuestExistsException {
        try {
            em.persist(guest);
            em.flush();
            return guest.getCustomerId();
        } catch(PersistenceException ex) {
           if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new GuestExistsException();
                } else {
                    throw new GuestExistsException(ex.getMessage()); // UnknownPersistenceException, but just using this exception for simplicity
                }
            } else {
               throw new GuestExistsException(ex.getMessage()); // UnknownPersistenceException, but just using this exception for simplicity
           }
        }
    }
    
    @Override
    public List<Guest> retrieveAllGuests() {
        Query query = em.createQuery("SELECT g from Guest g");
        List<Guest> guests = query.getResultList();
        for (Guest guest : guests) {
            guest.getReservations().size();
        }
        return guests;
    }
    
    @Override
    public Guest retrieveGuestByGuestId(Long guestId) throws GuestDNEException {
        Guest guest = em.find(Guest.class, guestId); 
        
        if (guest != null) {
            guest.getReservations().size(); // trigger lazy fetching
            return guest;
        } else {
            throw new GuestDNEException("Guest does not exist: " + guestId);
        }
    }
    
    @Override
    public Guest retrieveGuestByEmail(String email) throws GuestDNEException {
        Query query = em.createQuery("SELECT g from Guest g WHERE g.email = :inEmail");
        query.setParameter("inEmail", email);
        
        try {
            return (Guest)query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new GuestDNEException("Guest email " + email + " does not exist!");
        }
    }
    
    @Override
    public Guest guestLogin(String email, String password) throws InvalidLoginCredentialException, GuestDNEException {
        try {
            Guest g = retrieveGuestByEmail(email);
            
            if (g.getPassword().equals(password)) {
                // preload any lazy data if needed
                g.getReservations().size(); // trigger lazy fetching
                return g;
            } else {
                throw new InvalidLoginCredentialException("Guest email or password is incorrect!");
            }
        } catch (GuestDNEException ex) {
            throw new GuestDNEException("Guest email does not exist!");
        }
    }
}

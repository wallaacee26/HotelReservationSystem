/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Reservation;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.GuestDNEException;
import util.exception.ReservationDNEException;
import util.exception.ReservationExistsException;

/**
 *
 * @author wallace
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private GuestSessionBeanLocal guestSBLocal;
    
    @Override
    public Long createNewReservation(Reservation reservation) throws ReservationExistsException { // not sure if need this exception
        try {
            em.persist(reservation);
            em.flush();
            return reservation.getReservationId();
        } catch(PersistenceException ex) {
           if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new ReservationExistsException();
                } else {
                    throw new ReservationExistsException(ex.getMessage());
                }
            } else {
               throw new ReservationExistsException(ex.getMessage());
           }
        }
    }
    
    @Override
    public List<Reservation> retrieveAllReservations() {
        Query query = em.createQuery("SELECT r from Reservation r");
        return query.getResultList();
    }
    
    @Override
    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationDNEException {
        Reservation reservation = em.find(Reservation.class, reservationId); 
        
        if (reservation != null) {
            reservation.getReservedRooms().size(); // trigger lazy fetching
            return reservation;
        } else {
            throw new ReservationDNEException("Reservation does not exist: " + reservationId);
        }
    }
    
    @Override
    public List<Reservation> retrieveAllReservationsOfGuestId(Long guestId) throws GuestDNEException {
        try {
            Guest guest = guestSBLocal.retrieveGuestByGuestId(guestId);
            List<Reservation> reservations = em.createQuery("SELECT r from Reservation r WHERE r.guest = :inGuest")
                .setParameter("inGuest", guest)
                .getResultList();
            for (Reservation reservation : reservations) {
                reservation.getReservedRooms().size(); // trigger lazy fetching
            }
            return reservations;
        } catch (GuestDNEException ex) {
            throw new GuestDNEException(ex.getMessage());
        }
    }
    
    @Override
    public void deleteReservationByReservationId(Long reservationId) throws ReservationDNEException {
        try {
            Reservation reservation = retrieveReservationByReservationId(reservationId);
            
            em.remove(reservation);
            
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException("Reservation " + reservationId + " does not exist!");
        }
    }
}

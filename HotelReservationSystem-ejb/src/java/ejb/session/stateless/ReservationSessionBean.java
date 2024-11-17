/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import entity.Partner;
import entity.Reservation;
import entity.ReservedRoom;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.GuestDNEException;
import util.exception.PartnerDNEException;
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
    @EJB
    private PartnerSessionBeanLocal partnerSBLocal;
    
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
        List<Reservation> reservations = query.getResultList();
        for (Reservation reservation : reservations) {
            reservation.getReservedRooms().size();
        }
        return reservations;
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
            List<Reservation> reservations = em.createQuery("SELECT r from Reservation r WHERE r.customer = :inCustomer")
                .setParameter("inCustomer", guest) //
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
    public List<Reservation> retrieveAllReservationsOfPartnerId(Long partnerId) throws PartnerDNEException {
        try {
            Partner partner = partnerSBLocal.retrievePartnerByPartnerId(partnerId);
            List<Reservation> reservations = em.createQuery("SELECT r from Reservation r WHERE r.partner = :inPartner")
                .setParameter("inPartner", partner) //
                .getResultList();
            for (Reservation reservation : reservations) {
                reservation.getReservedRooms().size(); // trigger lazy fetching
            }
            return reservations;
        } catch (PartnerDNEException ex) {
            throw new PartnerDNEException(ex.getMessage());
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
    
    @Override
    public void associateReservationWithGuest(Long reservationId, Long guestId) throws ReservationDNEException, GuestDNEException {
        try {
            Reservation reservation = retrieveReservationByReservationId(reservationId);
            Guest guest = guestSBLocal.retrieveGuestByGuestId(guestId);
            
            // association
            reservation.setCustomerOrGuest(guest);
            guest.getReservations().add(reservation);
            
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException(ex.getMessage());
        } catch (GuestDNEException ex) {
            throw new GuestDNEException(ex.getMessage());
        }
    }
    
    @Override
    public void associateReservationWithPartner(Long reservationId, Long partnerId) throws ReservationDNEException, PartnerDNEException {
        try {
            Reservation reservation = retrieveReservationByReservationId(reservationId);
            Partner partner = partnerSBLocal.retrievePartnerByPartnerId(partnerId);
            
            // association
            reservation.setPartner(partner);
            partner.getReservations().add(reservation);
            
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException(ex.getMessage());
        } catch (PartnerDNEException ex) {
            throw new PartnerDNEException(ex.getMessage());
        }
    }
    
    @Override
    public void updateReservationBookingAmount(Long reservationId, BigDecimal bookingAmount) throws ReservationDNEException {
        try {
            Reservation reservation = retrieveReservationByReservationId(reservationId);
            
            reservation.setBookingPrice(bookingAmount);
            
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException(ex.getMessage());
        }
    }
    
    // for web service
    @Override
    public String getStringOfCheckInDate(Long reservationId) throws ReservationDNEException {
        try {
            // may have cut the relationship over webservice
            Reservation reservation = retrieveReservationByReservationId(reservationId);
            return reservation.getReservedRooms().get(0).getCheckInDate().toString();
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException(ex.getMessage());
        }
    }
    @Override
    public String getStringOfCheckOutDate(Long reservationId) throws ReservationDNEException {
        try {
            // may have cut the relationship over the webservice
            Reservation reservation = retrieveReservationByReservationId(reservationId);
            return reservation.getReservedRooms().get(0).getCheckOutDate().toString();
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException(ex.getMessage());
        }
    }
}

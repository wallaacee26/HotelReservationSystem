/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Reservation;
import entity.ReservedRoom;
import entity.Room;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.exception.PartnerDNEException;
import util.exception.ReservationDNEException;
import util.exception.ReservationExistsException;

/**
 *
 * @author wallace
 */
@WebService(serviceName = "ReservationWebService")
@Stateless()
public class ReservationWebService {

    // used to detach entity instances before nullifying relationship (cyclic reference problem)
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;
    
    @WebMethod(operationName = "createNewReservation")
    public Long createNewReservation(@WebParam(name = "reservation") Reservation reservation) throws ReservationExistsException { // not sure if need this exception
        try {
            return reservationSessionBeanLocal.createNewReservation(reservation);
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
    
    @WebMethod(operationName = "retrieveAllReservations")
    public List<Reservation> retrieveAllReservations() {
        List<Reservation> reservations = reservationSessionBeanLocal.retrieveAllReservations();
        for (Reservation reservation : reservations) {
            em.detach(reservation); // to cut off relationship to prevent changes to database
            for (ReservedRoom reservedRoom : reservation.getReservedRooms()) {
                em.detach(reservedRoom);
                reservedRoom.setReservation(null);
                reservedRoom.setRoom(null);
                reservedRoom.setRoomType(null);
            }
            if (reservation.getCustomerOrGuest() != null) {
                em.detach(reservation.getCustomerOrGuest());
                reservation.getCustomerOrGuest().getReservations().clear();
            }
            if (reservation.getPartner() != null) {
                em.detach(reservation.getPartner());
                reservation.getPartner().getReservations().clear();
            }
        }
        
        return reservations;
    }
    
    @WebMethod(operationName = "retrieveReservationByReservationId")
    public Reservation retrieveReservationByReservationId(@WebParam(name = "reservationId") Long reservationId) throws ReservationDNEException {
        try {
            Reservation reservation = reservationSessionBeanLocal.retrieveReservationByReservationId(reservationId);
            em.detach(reservation); // to cut off relationship to prevent changes to database
            for (ReservedRoom reservedRoom : reservation.getReservedRooms()) {
                em.detach(reservedRoom);
                reservedRoom.setReservation(null);
                // reservedRoom.setRoom(null); // cannot set this to null, need to get for next method call in web client
                reservedRoom.setRoomType(null);
                
                if (reservedRoom.getRoom() != null) { // if null, then ignore (already set to null)
                    Room assignedRoom = reservedRoom.getRoom();
                    em.detach(assignedRoom);
                    assignedRoom.getReservedRooms().clear();
                    assignedRoom.setRoomType(null);
                }
                    
            }
            if (reservation.getCustomerOrGuest() != null) {
                em.detach(reservation.getCustomerOrGuest());
                reservation.getCustomerOrGuest().getReservations().clear();
            }
            if (reservation.getPartner() != null) {
                em.detach(reservation.getPartner());
                reservation.getPartner().getReservations().clear();
            }
            return reservation;
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException("Reservation does not exist: " + reservationId);
        }
    }
    
    @WebMethod(operationName = "retrieveAllReservationsOfPartnerId")
    public List<Reservation> retrieveAllReservationsOfPartnerId(@WebParam(name = "partnerId") Long partnerId) throws PartnerDNEException {
        try {
            List<Reservation> reservations = reservationSessionBeanLocal.retrieveAllReservationsOfPartnerId(partnerId);
            for (Reservation reservation : reservations) {
                em.detach(reservation); // to cut off relationship to prevent changes to database
                for (ReservedRoom reservedRoom : reservation.getReservedRooms()) {
                    em.detach(reservedRoom);
                    reservedRoom.setReservation(null);
                    // reservedRoom.setRoom(null); // cannot set this to null, need to get for next method call in web client
                    reservedRoom.setRoomType(null);

                    if (reservedRoom.getRoom() != null) { // if null, then ignore (already set to null)
                        Room assignedRoom = reservedRoom.getRoom();
                        em.detach(assignedRoom);
                        assignedRoom.getReservedRooms().clear();
                        assignedRoom.setRoomType(null);
                    }
                }
                if (reservation.getCustomerOrGuest() != null) {
                    em.detach(reservation.getCustomerOrGuest());
                    reservation.getCustomerOrGuest().getReservations().clear();
                }
                if (reservation.getPartner() != null) {
                    em.detach(reservation.getPartner());
                    reservation.getPartner().getReservations().clear();
                }
            }
            return reservations;
        } catch (PartnerDNEException ex) {
            throw new PartnerDNEException(ex.getMessage());
        }
    }
    
    @WebMethod(operationName = "associateReservationWithPartner")
    public void associateReservationWithPartner(@WebParam(name = "reservationId") Long reservationId, @WebParam(name = "partnerId") Long partnerId)
            throws ReservationDNEException, PartnerDNEException {
        try {
            reservationSessionBeanLocal.associateReservationWithPartner(reservationId, partnerId);
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException(ex.getMessage());
        } catch (PartnerDNEException ex) {
            throw new PartnerDNEException(ex.getMessage());
        }
    }
    
    @WebMethod(operationName = "updateReservationBookingAmount")
    public void updateReservationBookingAmount(@WebParam(name = "reservationId") Long reservationId, @WebParam(name = "bookingAmount") BigDecimal bookingAmount)
            throws ReservationDNEException {
        try {
            reservationSessionBeanLocal.updateReservationBookingAmount(reservationId, bookingAmount);
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException(ex.getMessage());
        }
    }
    
    @WebMethod(operationName = "getStringOfCheckInDate")
    public String getStringOfCheckInDate(@WebParam(name = "reservationId") Long reservationId) throws ReservationDNEException {
        try {
            return reservationSessionBeanLocal.getStringOfCheckOutDate(reservationId);
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException(ex.getMessage());
        }
    }
    @WebMethod(operationName = "getStringOfCheckOutDate")
    public String getStringOfCheckOutDate(@WebParam(name = "reservationId") Long reservationId) throws ReservationDNEException {
        try {
            return reservationSessionBeanLocal.getStringOfCheckInDate(reservationId);
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException(ex.getMessage());
        }
    }
}

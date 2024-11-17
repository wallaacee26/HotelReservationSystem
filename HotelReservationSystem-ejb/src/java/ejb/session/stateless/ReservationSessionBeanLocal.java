/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.ReservedRoom;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Local;
import util.exception.GuestDNEException;
import util.exception.PartnerDNEException;
import util.exception.ReservationDNEException;
import util.exception.ReservationExistsException;

/**
 *
 * @author wallace
 */
@Local
public interface ReservationSessionBeanLocal {
    public Long createNewReservation(Reservation reservation) throws ReservationExistsException;
    
    public List<Reservation> retrieveAllReservations();
    
    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationDNEException;
    
    public List<Reservation> retrieveAllReservationsOfGuestId(Long guestId) throws GuestDNEException;
    
    public List<Reservation> retrieveAllReservationsOfPartnerId(Long partnerId) throws PartnerDNEException;
    
    public void deleteReservationByReservationId(Long reservationId) throws ReservationDNEException;
    
    public void associateReservationWithGuest(Long reservationId, Long guestId) throws ReservationDNEException, GuestDNEException;
    
    public void associateReservationWithPartner(Long reservationId, Long partnerId) throws ReservationDNEException, PartnerDNEException;
    
    public void updateReservationBookingAmount(Long reservationId, BigDecimal bookingAmount) throws ReservationDNEException;
    
    public String getStringOfCheckInDate(Long reservationId) throws ReservationDNEException;
    public String getStringOfCheckOutDate(Long reservationId) throws ReservationDNEException;
}

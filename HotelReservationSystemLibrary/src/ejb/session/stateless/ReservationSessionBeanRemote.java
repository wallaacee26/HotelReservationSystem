/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Reservation;
import java.util.List;
import javax.ejb.Remote;
import util.exception.GuestDNEException;
import util.exception.ReservationDNEException;
import util.exception.ReservationExistsException;

/**
 *
 * @author wallace
 */
@Remote
public interface ReservationSessionBeanRemote {
    public Long createNewReservation(Reservation reservation) throws ReservationExistsException;
    
    public List<Reservation> retrieveAllReservations();
    
    public Reservation retrieveReservationByReservationId(Long reservationId) throws ReservationDNEException;
    
    public List<Reservation> retrieveAllReservationsOfGuestId(Long guestId) throws GuestDNEException;
    
    public void deleteReservationByReservationId(Long reservationId) throws ReservationDNEException;
}

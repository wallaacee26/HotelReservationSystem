/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Guest;
import entity.Reservation;
import entity.ReservedRoom;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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

    
    
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello") // RENAME OPERATIONNAME TO THE METHOD NAME CALLED IN WEB CLIENT
    public String hello(@WebParam(name = "name") String txt) { // IF NO NEED PARAMETER, LEAVE EMPTY
        return "Hello " + txt + " !";
        
        // List<Record> records = recordSessionBeanLocal.retrieveAllRecords(); --> use local interface to call SB methods
        /*
        for (Record record : records) {
            em.detach(record); // to cut off relationship to prevent changes to database
            for (RecordVersion recordVersion : record.getRecordVersions()) {
                em.detach(recordVersion);
                recordVersion.setRecord(null); // record --- recordVersion : 1 -- *
            }
            em.detach(record.getCurrentRecordVersion());
        }
        return records;
        */
    }
    
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
    
    /*
    @WebMethod(operationName = "retrieveAllReservations")
    public List<Reservation> retrieveAllReservations() {
        List<Reservation> reservations = reservationSessionBeanLocal.retrieveAllReservations();
        for (Reservation reservation : reservations) {
            em.detach(reservation); // to cut off relationship to prevent changes to database
            for (ReservedRoom reservedRoom : reservation.getReservedRooms()) {
                em.detach(reservedRoom);
                reservedRoom.setReservation(null); // impossible, mandatory constraint
                reservedRoom.setRoom(null);
                reservedRoom.setRoomType(null); // impossible, mandatory constraint
            }
            if (reservation.getGuest() != null) {
                em.detach(reservation.getGuest());
                reservation.setGuest(null);
            }
            if (reservation.getPartner() != null) {
                em.detach(reservation.getPartner());
                reservation.setPartner(null);
            }
        }
        
        return reservations;
    }
    
    @WebMethod(operationName = "retrieveReservationByReservationId")
    public Reservation retrieveReservationByReservationId(@WebParam(name = "reservationId") Long reservationId) throws ReservationDNEException {
        try {
            Reservation reservation = reservationSessionBeanLocal.retrieveReservationByReservationId(reservationId);
            em.detach(reservation);
            for (ReservedRoom reservedRoom : reservation.getReservedRooms()) {
                em.detach(reservedRoom);
                reservedRoom.setReservation(null); // impossible, mandatory constraint
                reservedRoom.setRoom(null);
                reservedRoom.setRoomType(null); // impossible, mandatory constraint
            }
            if (reservation.getGuest() != null) {
                em.detach(reservation.getGuest());
                reservation.setGuest(null);
            }
            if (reservation.getPartner() != null) {
                em.detach(reservation.getPartner());
                reservation.setPartner(null);
            }
            return reservation;
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException("Reservation does not exist: " + reservationId);
        }
    }
    
    @WebMethod(operationName = "retrieveAllReservationsOfGuestId")
    public List<Reservation> retrieveAllReservationsOfGuestId(@WebParam(name = "guestId") Long guestId) throws GuestDNEException {
        try {
            List<Reservation> reservations = reservationSessionBeanLocal.retrieveAllReservationsOfGuestId(guestId);
            for (Reservation reservation : reservations) {
                em.detach(reservation); // to cut off relationship to prevent changes to database
                for (ReservedRoom reservedRoom : reservation.getReservedRooms()) {
                    em.detach(reservedRoom);
                    reservedRoom.setReservation(null); // impossible, mandatory constraint
                    reservedRoom.setRoom(null);
                    reservedRoom.setRoomType(null); // impossible, mandatory constraint
                }
                if (reservation.getGuest() != null) {
                    em.detach(reservation.getGuest());
                    reservation.setGuest(null);
                }
                if (reservation.getPartner() != null) {
                    em.detach(reservation.getPartner());
                    reservation.setPartner(null);
                }
            }
            return reservations;
        } catch (GuestDNEException ex) {
            throw new GuestDNEException(ex.getMessage());
        }
    }
    */
}

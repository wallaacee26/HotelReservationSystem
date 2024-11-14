/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.ReservedRoomSessionBeanLocal;
import entity.Reservation;
import entity.ReservedRoom;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.datatype.XMLGregorianCalendar;
import util.exception.ReservationDNEException;
import util.exception.RoomTypeDNEException;

/**
 *
 * @author wallace
 */
@WebService(serviceName = "ReservedRoomWebService")
@Stateless()
public class ReservedRoomWebService {

    // used to detach entity instances before nullifying relationship (cyclic reference problem)
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB(name = "ReservedRoomSessionBeanLocal")
    private ReservedRoomSessionBeanLocal reservedRoomSessionBeanLocal;
    
    
    @WebMethod(operationName = "createNewReservedRoom")
    public Long createNewReservedRoom(@WebParam(name = "reservedRoom") ReservedRoom reservedRoom,
                                      @WebParam(name = "reservationId") Long reservationId,
                                      @WebParam(name = "roomTypeId") Long roomTypeId)
            throws ReservationDNEException, RoomTypeDNEException {
        try {
            return reservedRoomSessionBeanLocal.createNewReservedRoom(reservedRoom, reservationId, roomTypeId);
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException(ex.getMessage());
        } catch (RoomTypeDNEException ex) {
            throw new ReservationDNEException(ex.getMessage());
        }
    }
    
    @WebMethod(operationName = "retrieveAllReservedRooms")
    public List<ReservedRoom> retrieveAllReservedRooms() {
        List<ReservedRoom> reservedRooms = reservedRoomSessionBeanLocal.retrieveAllReservedRooms();
        for (ReservedRoom reservedRoom : reservedRooms) {
            em.detach(reservedRoom);
            
            em.detach(reservedRoom.getReservation());
            reservedRoom.getReservation().setCustomerOrGuest(null);
            reservedRoom.getReservation().setPartner(null);
            reservedRoom.getReservation().getReservedRooms().clear();
            
            em.detach(reservedRoom.getRoom());
            reservedRoom.getRoom().getReservedRooms().clear();
            reservedRoom.getRoom().setRoomType(null);
            
            em.detach(reservedRoom.getRoomType());
            reservedRoom.getRoomType().getReservedRooms().clear();
            reservedRoom.getRoomType().getRoomRates().clear();
            reservedRoom.getRoomType().getRooms().clear();
        }
        return reservedRooms;
    }
    
    @WebMethod(operationName = "retrieveReservedRoomsByReservationId")
    public List<ReservedRoom> retrieveReservedRoomsByReservationId(@WebParam(name = "reservationId") Long reservationId) throws ReservationDNEException {
        try {
            List<ReservedRoom> reservedRooms = reservedRoomSessionBeanLocal.retrieveReservedRoomsByReservationId(reservationId);
            for (ReservedRoom reservedRoom : reservedRooms) {
                em.detach(reservedRoom);

                em.detach(reservedRoom.getReservation());
                reservedRoom.getReservation().setCustomerOrGuest(null);
                reservedRoom.getReservation().setPartner(null);
                reservedRoom.getReservation().getReservedRooms().clear();

                em.detach(reservedRoom.getRoom());
                reservedRoom.getRoom().getReservedRooms().clear();
                reservedRoom.getRoom().setRoomType(null);

                em.detach(reservedRoom.getRoomType());
                reservedRoom.getRoomType().getReservedRooms().clear();
                reservedRoom.getRoomType().getRoomRates().clear();
                reservedRoom.getRoomType().getRooms().clear();
            }
            return reservedRooms;
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException(ex.getMessage());
        }
    }
    
    @WebMethod(operationName = "associateReservedRoomWithDatesWebService")
    public ReservedRoom associateReservedRoomWithDatesWebService(
            @WebParam(name = "reservedRoom") ReservedRoom reservedRoom,
            @WebParam(name = "checkInDate") XMLGregorianCalendar checkInDate,
            @WebParam(name = "checkOutDate") XMLGregorianCalendar checkOutDate) {
        
        //convert XMLGregorianCalender to localdate
        LocalDate checkInLocalDate = checkInDate.toGregorianCalendar().toZonedDateTime().toLocalDate();
        LocalDate checkOutLocalDate = checkOutDate.toGregorianCalendar().toZonedDateTime().toLocalDate();
        return reservedRoomSessionBeanLocal.associateReservedRoomWithDatesWebService(reservedRoom, checkInLocalDate, checkOutLocalDate);
    }
}

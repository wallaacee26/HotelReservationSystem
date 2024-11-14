/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.ReservedRoom;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.datatype.XMLGregorianCalendar;
import util.exception.RoomTypeDNEException;

/**
 *
 * @author wallace
 */
@WebService(serviceName = "RoomTypeWebService")
@Stateless()
public class RoomTypeWebService {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;
        
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @WebMethod(operationName = "retrieveAllRoomTypes")
    public List<RoomType> retrieveAllRoomTypes() {
        List<RoomType> roomTypes = roomTypeSessionBeanLocal.retrieveAllRoomTypes();
        for (RoomType roomType : roomTypes) {
            em.detach(roomType);
            
            if (roomType.getHigherRoomType() != null) {
                em.detach(roomType.getHigherRoomType());
                for (RoomRate roomRate : roomType.getHigherRoomType().getRoomRates()) {
                    em.detach(roomRate);
                    roomRate.getRoomTypes().clear();
                }
                for (ReservedRoom reservedRoom : roomType.getHigherRoomType().getReservedRooms()) {
                    em.detach(reservedRoom);
                    reservedRoom.setReservation(null);
                    reservedRoom.setRoom(null);
                    reservedRoom.setRoomType(null);
                }
                for (Room room : roomType.getHigherRoomType().getRooms()) {
                    em.detach(room);
                    room.getReservedRooms().clear();
                    room.setRoomType(null);
                }
                
                roomType.getHigherRoomType().setHigherRoomType(null);
            }
                
            for (RoomRate roomRate : roomType.getRoomRates()) {
                em.detach(roomRate);
                roomRate.getRoomTypes().clear();
            }
            for (ReservedRoom reservedRoom : roomType.getReservedRooms()) {
                em.detach(reservedRoom);
                reservedRoom.setReservation(null);
                reservedRoom.setRoom(null);
                reservedRoom.setRoomType(null);
            }
            for (Room room : roomType.getRooms()) {
                em.detach(room);
                room.getReservedRooms().clear();
                room.setRoomType(null);
            }
        }
        return roomTypes;
    }
    
    @WebMethod(operationName = "retrieveRoomTypeByRoomTypeName")
    public RoomType retrieveRoomTypeByRoomTypeName(@WebParam(name = "roomTypeName") String roomTypeName) throws RoomTypeDNEException {
        try {
            RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(roomTypeName);
            em.detach(roomType);
            
                
                
            if (roomType.getHigherRoomType() != null) {
                em.detach(roomType.getHigherRoomType());
                for (RoomRate roomRate : roomType.getHigherRoomType().getRoomRates()) {
                    em.detach(roomRate);
                    roomRate.getRoomTypes().clear();
                }
                for (ReservedRoom reservedRoom : roomType.getHigherRoomType().getReservedRooms()) {
                    em.detach(reservedRoom);
                    reservedRoom.setReservation(null);
                    reservedRoom.setRoom(null);
                    reservedRoom.setRoomType(null);
                }
                for (Room room : roomType.getHigherRoomType().getRooms()) {
                    em.detach(room);
                    room.getReservedRooms().clear();
                    room.setRoomType(null);
                }
                
                roomType.getHigherRoomType().setHigherRoomType(null);
            }
                
            for (RoomRate roomRate : roomType.getRoomRates()) {
                em.detach(roomRate);
                roomRate.getRoomTypes().clear();
            }
            for (ReservedRoom reservedRoom : roomType.getReservedRooms()) {
                em.detach(reservedRoom);
                reservedRoom.setReservation(null);
                reservedRoom.setRoom(null);
                reservedRoom.setRoomType(null);
            }
            for (Room room : roomType.getRooms()) {
                em.detach(room);
                room.getReservedRooms().clear();
                room.setRoomType(null);
            }
            return roomType;
        } catch (RoomTypeDNEException ex) {
            throw new RoomTypeDNEException(ex.getMessage());
        }
    }
    
    @WebMethod(operationName = "retrieveRoomTypeByRoomTypeId")
    public RoomType retrieveRoomTypeByRoomTypeId(@WebParam(name = "roomTypeId") Long roomTypeId) throws RoomTypeDNEException {
        try {
            RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeId(roomTypeId);
            em.detach(roomType);
            
            if (roomType.getHigherRoomType() != null) {
                em.detach(roomType.getHigherRoomType());
                for (RoomRate roomRate : roomType.getHigherRoomType().getRoomRates()) {
                    em.detach(roomRate);
                    roomRate.getRoomTypes().clear();
                }
                for (ReservedRoom reservedRoom : roomType.getHigherRoomType().getReservedRooms()) {
                    em.detach(reservedRoom);
                    reservedRoom.setReservation(null);
                    reservedRoom.setRoom(null);
                    reservedRoom.setRoomType(null);
                }
                for (Room room : roomType.getHigherRoomType().getRooms()) {
                    em.detach(room);
                    room.getReservedRooms().clear();
                    room.setRoomType(null);
                }
                
                roomType.getHigherRoomType().setHigherRoomType(null);
            }
                    
            for (RoomRate roomRate : roomType.getRoomRates()) {
                em.detach(roomRate);
                roomRate.getRoomTypes().clear();
            }
            for (ReservedRoom reservedRoom : roomType.getReservedRooms()) {
                em.detach(reservedRoom);
                reservedRoom.setReservation(null);
                reservedRoom.setRoom(null);
                reservedRoom.setRoomType(null);
            }
            for (Room room : roomType.getRooms()) {
                em.detach(room);
                room.getReservedRooms().clear();
                room.setRoomType(null);
            }
            return roomType;
        } catch (RoomTypeDNEException ex) {
            throw new RoomTypeDNEException(ex.getMessage());
        }
    }
    
    // note the parameter type has changed from localdate to date for web
    @WebMethod(operationName = "searchAvailableRoomTypesWithNumberOfRooms")
    public List<Integer> searchAvailableRoomTypesWithNumberOfRooms(@WebParam(name = "checkInDate") XMLGregorianCalendar checkInDate, @WebParam(name = "checkOutDate") XMLGregorianCalendar checkOutDate) {
        //convert XMLGregorianCalender to localdate
        LocalDate checkInLocalDate = checkInDate.toGregorianCalendar().toZonedDateTime().toLocalDate();
        LocalDate checkOutLocalDate = checkOutDate.toGregorianCalendar().toZonedDateTime().toLocalDate();
        return roomTypeSessionBeanLocal.searchAvailableRoomTypesWithNumberOfRooms(checkInLocalDate, checkOutLocalDate);
    }
    
    @WebMethod(operationName = "checkAvailabilityForRoomType")
    public boolean checkAvailabilityForRoomType(
            @WebParam(name = "roomTypeName") String roomTypeName, 
            @WebParam(name = "checkInDate") XMLGregorianCalendar checkInDate,
            @WebParam(name = "checkOutDate") XMLGregorianCalendar checkOutDate) throws RoomTypeDNEException {
        try {
            //convert XMLGregorianCalender to localdate
            LocalDate checkInLocalDate = checkInDate.toGregorianCalendar().toZonedDateTime().toLocalDate();
            LocalDate checkOutLocalDate = checkOutDate.toGregorianCalendar().toZonedDateTime().toLocalDate();
            return roomTypeSessionBeanLocal.checkAvailabilityForRoomType(roomTypeName, checkInLocalDate, checkOutLocalDate);
        } catch (RoomTypeDNEException ex) {
            throw new RoomTypeDNEException(ex.getMessage());
        }
    }
}

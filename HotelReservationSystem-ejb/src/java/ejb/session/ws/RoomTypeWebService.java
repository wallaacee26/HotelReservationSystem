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
import java.util.ArrayList;
import java.util.Collections;
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
    public RoomType retrieveRoomTypeByRoomTypeName(String roomTypeName) throws RoomTypeDNEException {
        try {
            RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(roomTypeName);
            em.detach(roomType);
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
    public RoomType retrieveRoomTypeByRoomTypeId(Long roomTypeId) throws RoomTypeDNEException {
        try {
            RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeId(roomTypeId);
            em.detach(roomType);
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
    
    @WebMethod(operationName = "searchAvailableRoomTypesWithNumberOfRooms")
    public List<Integer> searchAvailableRoomTypesWithNumberOfRooms(LocalDate checkInDate, LocalDate checkOutDate) {
        return roomTypeSessionBeanLocal.searchAvailableRoomTypesWithNumberOfRooms(checkInDate, checkOutDate);
    }
    
    @WebMethod(operationName = "checkAvailabilityForRoomType")
    public boolean checkAvailabilityForRoomType(String roomTypeName, LocalDate checkInDate, LocalDate checkOutDate) throws RoomTypeDNEException {
        try {
            return roomTypeSessionBeanLocal.checkAvailabilityForRoomType(roomTypeName, checkInDate, checkOutDate);
        } catch (RoomTypeDNEException ex) {
            throw new RoomTypeDNEException(ex.getMessage());
        }
    }
}

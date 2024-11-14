/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomType;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import util.exception.RoomTypeDNEException;
import util.exception.RoomTypeExistsException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author wallace
 */
@Local
public interface RoomTypeSessionBeanLocal {
    public Long createNewRoomType(RoomType roomType) throws RoomTypeExistsException;
    
    public List<RoomType> retrieveAllRoomTypes();
    
    public RoomType retrieveRoomTypeByRoomTypeName(String roomTypeName) throws RoomTypeDNEException;
    
    public RoomType retrieveRoomTypeByRoomTypeId(Long roomTypeId) throws RoomTypeDNEException;
    
    public RoomType updateRoomType(String roomTypeName, RoomType newRoomType) throws RoomTypeDNEException, UpdateRoomTypeException;
    
    public void deleteRoomType(String roomTypeName) throws RoomTypeDNEException;
    
    public List<Integer> searchAvailableRoomTypesWithNumberOfRooms(LocalDate checkInDate, LocalDate checkOutDate);
    
    public int findNumberOfAvailableRoomsForRoomType(String roomTypeName, LocalDate checkInDate, LocalDate checkOutDate) throws RoomTypeDNEException;
    
    public boolean checkAvailabilityForRoomType(String roomTypeName, LocalDate checkInDate, LocalDate checkOutDate) throws RoomTypeDNEException;

    public void setNextHigherRoomType(String currentTypeName, String nextHigherTypeName);
    
    public List<Integer> searchAvailableRoomTypesWithNumberOfRoomsWebService(Date checkInDate, Date checkOutDate);
}

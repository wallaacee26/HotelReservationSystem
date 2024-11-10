/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.Room;
import java.util.List;
import javax.ejb.Local;
import util.exception.RoomDNEException;
import util.exception.RoomExistsException;
import util.exception.RoomTypeDNEException;
import util.exception.RoomTypeDisabledException;
import util.exception.UpdateRoomException;

/**
 *
 * @author wallace
 */
@Local
public interface RoomSessionBeanLocal {
    public Long createNewRoom(Room room, String roomTypeName) throws RoomExistsException, RoomTypeDNEException, RoomTypeDisabledException;
    
    public List<Room> retrieveAllRooms();
    
    public Room retrieveRoomByRoomNumber(String roomNumber) throws RoomDNEException;
    
    public Room updateRoom(String roomNumber, Room newRoom) throws RoomDNEException, UpdateRoomException;
    
    public void deleteRoom(String roomNumber) throws RoomDNEException;
}

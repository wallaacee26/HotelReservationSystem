package ejb.session.stateless;

import entity.Room;
import java.util.List;
import javax.ejb.Remote;
import util.exception.RoomDNEException;
import util.exception.RoomExistsException;
import util.exception.RoomTypeDNEException;
import util.exception.RoomTypeDisabledException;
import util.exception.UpdateRoomException;

/**
 *
 * @author wallace
 */
@Remote
public interface RoomSessionBeanRemote {
    public Long createNewRoom(Room room, String roomTypeName) throws RoomExistsException, RoomTypeDNEException, RoomTypeDisabledException;
    
    public List<Room> retrieveAllRooms();
    
    public Room retrieveRoomByRoomNumber(String roomNumber) throws RoomDNEException;
    
    public Room updateRoom(String roomNumber, Room newRoom) throws RoomDNEException, UpdateRoomException;

    public void deleteRoom(String roomNumber) throws RoomDNEException;
}

package ejb.session.stateless;

import entity.Room;
import java.util.List;
import javax.ejb.Remote;
import util.exception.RoomDNEException;
import util.exception.RoomExistsException;

/**
 *
 * @author wallace
 */
@Remote
public interface RoomSessionBeanRemote {
    public Long createNewRoom(Room room) throws RoomExistsException;
    
    public List<Room> retrieveAllRooms();
    
    public Room retrieveRoomByRoomNumber(String roomNumber) throws RoomDNEException;
    
    public Room updateRoom(String roomNumber, Room newRoom) throws RoomDNEException, RoomExistsException;
}

package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Remote;
import util.exception.RoomTypeDNEException;
import util.exception.RoomTypeExistsException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author wallace
 */
@Remote
public interface RoomTypeSessionBeanRemote {
    public Long createNewRoomType(RoomType roomType) throws RoomTypeExistsException;
    
    public List<RoomType> retrieveAllRoomTypes();
    
    public RoomType retrieveRoomTypeByRoomTypeName(String roomTypeName) throws RoomTypeDNEException;
    
    public RoomType updateRoomType(String roomTypeName, RoomType newRoomType) throws RoomTypeDNEException, UpdateRoomTypeException;

    public void deleteRoomType(String roomTypeName) throws RoomTypeDNEException;
}

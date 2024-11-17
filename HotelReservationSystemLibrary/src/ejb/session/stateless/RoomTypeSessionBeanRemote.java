package ejb.session.stateless;

import entity.RoomType;
import java.time.LocalDate;
import java.util.Date;
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
    
    public RoomType retrieveRoomTypeByRoomTypeId(Long roomTypeId) throws RoomTypeDNEException;
    
    public RoomType updateRoomType(String roomTypeName, RoomType newRoomType) throws RoomTypeDNEException, UpdateRoomTypeException;

    public void deleteRoomType(String roomTypeName) throws RoomTypeDNEException;
    
    public List<Integer> searchAvailableRoomTypesWithNumberOfRooms(LocalDate checkInDate, LocalDate checkOutDate);
    
    public boolean checkAvailabilityForRoomType(String roomTypeName, LocalDate checkInDate, LocalDate checkOutDate) throws RoomTypeDNEException;
    
    public List<Integer> searchAvailableRoomTypesWithNumberOfRoomsWebService(Date checkInDate, Date checkOutDate);
}

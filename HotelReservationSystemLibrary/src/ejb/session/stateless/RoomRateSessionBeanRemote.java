package ejb.session.stateless;

import entity.RoomRate;
import java.util.List;
import javax.ejb.Remote;
import util.exception.RoomRateDNEException;
import util.exception.RoomRateExistsException;

/**
 *
 * @author wallace
 */
@Remote
public interface RoomRateSessionBeanRemote {
    public Long createNewRoomRate(RoomRate roomRate) throws RoomRateExistsException;
    
    public List<RoomRate> retrieveAllRoomRates();
    
    public RoomRate retrieveRoomRateByRoomRateName(String roomRateName) throws RoomRateDNEException;
    
    public RoomRate updateRoomRate(String roomRateName, RoomRate newRoomRate) throws RoomRateDNEException, RoomRateExistsException;
}

package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Remote;
import util.exception.RoomRateDNEException;
import util.exception.RoomRateExistsException;
import util.exception.RoomTypeDNEException;
import util.exception.RoomTypeDisabledException;

/**
 *
 * @author wallace
 */
@Remote
public interface RoomRateSessionBeanRemote {
    public Long createNewRoomRate(RoomRate roomRate, String roomTypeName) throws RoomRateExistsException, RoomTypeDNEException, RoomTypeDisabledException;
    
    public List<RoomRate> retrieveAllRoomRates();
    
    public RoomRate retrieveRoomRateByRoomRateName(String roomRateName) throws RoomRateDNEException;
    
    public RoomRate updateRoomRate(String roomRateName, RoomRate newRoomRate) throws RoomRateDNEException;

    public void deleteRoomRate(String roomRateName) throws RoomRateDNEException;
    
    public BigDecimal calculateTotalRoomRate(String roomTypeName, LocalDate checkInDate, LocalDate checkOutDate) throws RoomTypeDNEException;
}

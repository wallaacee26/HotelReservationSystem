/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Local;
import util.exception.RoomRateDNEException;
import util.exception.RoomRateExistsException;
import util.exception.RoomTypeDNEException;
import util.exception.RoomTypeDisabledException;

/**
 *
 * @author wallace
 */
@Local
public interface RoomRateSessionBeanLocal {
    public Long createNewRoomRate(RoomRate roomRate, String roomTypeName) throws RoomRateExistsException, RoomTypeDNEException, RoomTypeDisabledException;
    
    public List<RoomRate> retrieveAllRoomRates();
    
    public RoomRate retrieveRoomRateByRoomRateName(String roomRateName) throws RoomRateDNEException;
    
    public RoomRate updateRoomRate(String roomRateName, RoomRate newRoomRate) throws RoomRateDNEException;
    
    public void deleteRoomRate(String roomRateName) throws RoomRateDNEException;
    
    public BigDecimal calculateTotalRoomRateWithPublishedRate(String roomTypeName, LocalDate checkInDate, LocalDate checkOutDate) throws RoomTypeDNEException;
            
    public BigDecimal calculateTotalRoomRateWithNormalRate(String roomTypeName, LocalDate checkInDate, LocalDate checkOutDate) throws RoomTypeDNEException;
}

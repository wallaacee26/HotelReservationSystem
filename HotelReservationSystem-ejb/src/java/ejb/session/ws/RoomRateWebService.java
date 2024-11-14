/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.RoomRateSessionBeanLocal;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.RoomTypeDNEException;

/**
 *
 * @author wallace
 */
@WebService(serviceName = "RoomRateWebService")
@Stateless()
public class RoomRateWebService {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBeanLocal;
    
    @WebMethod(operationName = "calculateTotalRoomRate")
    public BigDecimal calculateTotalRoomRate(
            @WebParam(name = "roomTypeName ") String roomTypeName,
            @WebParam(name = "checkInDate") LocalDate checkInDate,
            @WebParam(name = "checkOutDate") LocalDate checkOutDate)
            throws RoomTypeDNEException {
        return roomRateSessionBeanLocal.calculateTotalRoomRateWithNormalRate(roomTypeName, checkInDate, checkOutDate);
    }
}

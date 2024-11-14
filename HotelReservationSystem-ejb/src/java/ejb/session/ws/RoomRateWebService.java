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
import javax.xml.datatype.XMLGregorianCalendar;
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
    
    @WebMethod(operationName = "calculateTotalRoomRateWithNormalRate")
    public BigDecimal calculateTotalRoomRateWithNormalRate(
            @WebParam(name = "roomTypeName") String roomTypeName,
            @WebParam(name = "checkInDate") XMLGregorianCalendar checkInDate,
            @WebParam(name = "checkOutDate") XMLGregorianCalendar checkOutDate)
            throws RoomTypeDNEException {
        //convert XMLGregorianCalender to localdate
        LocalDate checkInLocalDate = checkInDate.toGregorianCalendar().toZonedDateTime().toLocalDate();
        LocalDate checkOutLocalDate = checkOutDate.toGregorianCalendar().toZonedDateTime().toLocalDate();
        return roomRateSessionBeanLocal.calculateTotalRoomRateWithNormalRate(roomTypeName, checkInLocalDate, checkOutLocalDate);
    }
}

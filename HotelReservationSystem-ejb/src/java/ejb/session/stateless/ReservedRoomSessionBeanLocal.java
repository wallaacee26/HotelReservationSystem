/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionLocal.java to edit this template
 */
package ejb.session.stateless;

import entity.ReservedRoom;
import java.util.List;
import javax.ejb.Local;
import util.exception.ReservationDNEException;
import util.exception.RoomTypeDNEException;

/**
 *
 * @author yewkhang
 */
@Local
public interface ReservedRoomSessionBeanLocal {
    public Long createNewReservedRoom(ReservedRoom reservedRoom, Long reservationId, Long roomTypeId) throws ReservationDNEException, RoomTypeDNEException;
    
    public List<ReservedRoom> retrieveAllReservedRooms();
    
    public List<ReservedRoom> retrieveReservedRoomsByReservationId(Long reservationId) throws ReservationDNEException;
    
    public String generateExceptionReport();
}

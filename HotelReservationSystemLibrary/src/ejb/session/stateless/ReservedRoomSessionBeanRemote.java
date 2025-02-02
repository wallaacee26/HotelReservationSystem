/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.ReservedRoom;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.ejb.Remote;
import util.exception.ReservationDNEException;
import util.exception.RoomTypeDNEException;

/**
 *
 * @author yewkhang
 */
@Remote
public interface ReservedRoomSessionBeanRemote {
    public Long createNewReservedRoom(ReservedRoom reservedRoom, Long reservationId, Long roomTypeId) throws ReservationDNEException, RoomTypeDNEException;
    
    public List<ReservedRoom> retrieveAllReservedRooms();
    
    public List<ReservedRoom> retrieveReservedRoomsByReservationId(Long reservationId) throws ReservationDNEException;
    
    public String generateExceptionReport(LocalDate date);

    public void allocateRooms();
    
    public Long createNewReservedRoomWebService(ReservedRoom reservedRoom, Long reservationId, Long roomTypeId, LocalDate checkInDate, LocalDate checkOutDate) throws ReservationDNEException, RoomTypeDNEException;
    
    public void allocateRoomsForDate(LocalDate allocationDate);
}

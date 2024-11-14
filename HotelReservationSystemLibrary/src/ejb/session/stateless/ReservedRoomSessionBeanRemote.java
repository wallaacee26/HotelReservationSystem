/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.ReservedRoom;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author yewkhang
 */
@Remote
public interface ReservedRoomSessionBeanRemote {
    public Long createNewReservedRoom(ReservedRoom room);
    
    public List<ReservedRoom> retrieveAllReservedRooms();
    
    public String generateExceptionReport();
}

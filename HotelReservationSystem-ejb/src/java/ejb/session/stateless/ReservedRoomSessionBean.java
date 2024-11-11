/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.ReservedRoom;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author yewkhang
 */
@Stateless
public class ReservedRoomSessionBean implements ReservedRoomSessionBeanRemote, ReservedRoomSessionBeanLocal {
    
    @EJB
    private RoomSessionBeanLocal roomSessionBeanLocal;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    public Long createNewReservedRoom(ReservedRoom room) {
        // do association with Reservation?
        em.persist(room);
        em.flush();
        return room.getReservedRoomId();
    }
    
    public List<ReservedRoom> retrieveAllReservedRooms() {
        Query query = em.createQuery("SELECT r from ReservedRoom r");
        List<ReservedRoom> rooms = query.getResultList();
        // lazy load the data to get details
        for (ReservedRoom r : rooms) {
            r.getRoomType();
            r.getReservation();
        }
        return rooms;
    }
    
    public void allocateRooms(Date today) {
        List<ReservedRoom> reservedRoomsToAllocate = em.createQuery("SELECT r from ReservedRoom r WHERE r.checkInDate = :today")
                .setParameter("today", today)
                .getResultList();
        for (ReservedRoom reserveRoom : reservedRoomsToAllocate) {
            // get available rooms of the reserved room's type
            // loop through available rooms, assign to reservedRoomsToAllocate one by one. Do associations
            // if availableRooms returns an empty list
            // while current room type still has a nextRoomType, 
            // get the reserved room that cannot be allocated and try to search for the next available room type
            // if can get, do allocation to the reserved room and update isUpgraded to true
            // if cannot get, do nothing (assigned room will be null and isUpgraded = false)
            // go to the next reservedRoom 
        }

    }
    
    public void generateExceptionReport() {
        
    }

}

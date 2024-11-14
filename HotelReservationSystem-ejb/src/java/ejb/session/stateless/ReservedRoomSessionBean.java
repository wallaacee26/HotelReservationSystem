/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.ReservedRoom;
import entity.Room;
import entity.RoomType;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

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
    
    @Override
    public Long createNewReservedRoom(ReservedRoom room) {
        // do association with Reservation? yes, association done as new unmanaged instance in client
        em.persist(room);
        em.flush();
        return room.getReservedRoomId();
    }
    
    @Override
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
    
    @Schedule(hour = "*", minute = "*/1", info = "roomAllocationTimer")
    public void allocateRooms() {
        LocalDate today = LocalDate.now();
        // get reserved rooms that are being checked in today
        List<ReservedRoom> reservedRoomsToAllocate = em.createQuery("SELECT r from ReservedRoom r WHERE r.checkInDate = :today")
                .setParameter("today", today)
                .getResultList();
        int roomNumberToAllocate;
        for (roomNumberToAllocate = 0; roomNumberToAllocate < reservedRoomsToAllocate.size(); roomNumberToAllocate++) {
            ReservedRoom reserveRoom = reservedRoomsToAllocate.get(roomNumberToAllocate);
            // if the reservation room has not been allocated to a room yet
            if (reserveRoom.getRoom() == null) {
                RoomType roomType = reserveRoom.getRoomType();
                // get available rooms of the reserved room's type
                List<Room> availableRooms = roomSessionBeanLocal.retrieveAvailableRoomsTodayByRoomType(today, roomType.getRoomTypeName());
                // if have available rooms, assign to reservedRoomsToAllocate one by one. Do associations
                if (availableRooms.size() > roomNumberToAllocate) {
                    Room roomToAssign = availableRooms.get(roomNumberToAllocate);
                    // assign Room to Reserve Room
                    reserveRoom.setRoom(roomToAssign);
                    // add Reserve Room to the Room
                    roomToAssign.getReservedRooms().add(reserveRoom);
                    System.out.println("room allocated");
                } else { // no Rooms with desired RoomType
                    // get next higher room type
                    RoomType nextHigherRoomType = roomType.getHigherRoomType();
                    if (nextHigherRoomType != null) { // if have higher room type
                        List<Room> nextTierAvailableRooms = roomSessionBeanLocal.retrieveAvailableRoomsTodayByRoomType(today, nextHigherRoomType.getRoomTypeName());
                        if (nextTierAvailableRooms.size() > 0) {
                                Room upgradedRoom = nextTierAvailableRooms.get(0);
                                reserveRoom.setRoom(upgradedRoom);
                                reserveRoom.setIsUpgraded(true);
                                upgradedRoom.getReservedRooms().add(reserveRoom);
                        System.out.println("room allocated");
                        }
                    } else {
                        System.out.println("No room allocated! No higher room type available!");
                    }
                    
                    // else cannot upgrade anymore, do nothing
                }
            }
            
            // if availableRooms returns an empty list
            // while current room type still has a nextRoomType, 
            // get the reserved room that cannot be allocated and try to search for the next available room type
            // if can get, do allocation to the reserved room and update isUpgraded to true
            // if cannot get, do nothing (assigned room will be null and isUpgraded = false)
            // go to the next reservedRoom 
        }
        System.out.println("nothing happened!");
    }
    
    @Override
    public String generateExceptionReport() {
        LocalDate today = LocalDate.now();
        // get ReservedRooms where checkin is today where isUpgraded = true OR rr.room = null
        List<ReservedRoom> exceptions = em.createQuery("SELECT rr FROM ReservedRoom rr "
                + "WHERE (rr.room IS NULL OR rr.isUpgraded = TRUE) AND rr.checkInDate = :today")
                .setParameter("today", today)
                .getResultList();
        String exceptionReport = "Exception Reports for Today: \n";
        if (!exceptions.isEmpty()) {
            for (ReservedRoom rr : exceptions) {
                if(rr.isIsUpgraded()) {
                    exceptionReport += "Reserved Room: " + rr.getReservedRoomId() + " upgraded from " +
                            rr.getRoomType() + " to " + rr.getRoomType().getHigherRoomType() + 
                            ". Allocated Room Number " + rr.getRoom().getRoomNumber() + "\n";
                } else {
                    exceptionReport += "Reserved Room: " + rr.getReservedRoomId() + 
                            " has no next higher room type available. Room was not allocated! \n";
                }
            }
        }
        return exceptionReport;
    }

}

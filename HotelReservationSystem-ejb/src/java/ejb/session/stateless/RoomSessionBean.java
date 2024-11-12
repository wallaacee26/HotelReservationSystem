package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.RoomDNEException;
import util.exception.RoomExistsException;
import util.exception.RoomTypeDNEException;
import util.exception.RoomTypeDisabledException;
import util.exception.UpdateRoomException;

/**
 *
 * @author wallace
 */
@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;
    
    @Override
    public Long createNewRoom(Room room, String roomTypeName) throws RoomExistsException, RoomTypeDNEException, RoomTypeDisabledException {
        try {
            // associate Room Rate to the room type
            RoomType rt = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(roomTypeName);
            if (!rt.isDisabled()) {
                rt.getRooms().add(room);
                em.persist(room);
                em.flush();
                return room.getRoomId();
            } else {
                throw new RoomTypeDisabledException("Room type: " + roomTypeName + " has been disabled! Unable to create a new Room with this Room Type!");
            }
            
        } catch(PersistenceException ex) {
           if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new RoomExistsException(); // to be confirmed
                } else {
                    throw new RoomExistsException(ex.getMessage());
                }
            } else {
               throw new RoomExistsException(ex.getMessage());
            }
        } catch (RoomTypeDNEException ex) {
            throw new RoomTypeDNEException();
        }
    }
    
    public List<Room> retrieveAllRooms() {
        Query query = em.createQuery("SELECT r from Room r");
        List<Room> rooms = query.getResultList();
        // lazy load the data to get room type details
        for (Room r : rooms) {
            r.getRoomType();
        }
        return rooms;
    }
    
    public Room retrieveRoomByRoomNumber(String roomNumber) throws RoomDNEException {
        Query query = em.createQuery("SELECT r from Room r WHERE r.roomNumber = :roomNumber");
        query.setParameter("roomNumber", roomNumber);
        
        try {
            return (Room) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomDNEException("Room Number " + roomNumber + " does not exist!");
        }
    }
    
    public List<Room> retrieveAvailableRoomsTodayByRoomType(Date today, String roomTypeName) {
        // get rooms that are isAvailable OR have their checkout day as today, and have the desired room type
        Query query = em.createQuery("SELECT r FROM Room r JOIN r.roomType rt JOIN r.reservedRooms rr"
                + "WHERE (r.available = TRUE OR rr.checkOutDate = :today) "
                + "AND rt.roomTypeName = :roomTypeName");
        query.setParameter("today", today);
        query.setParameter("roomTypeName", roomTypeName);
                
        return query.getResultList();
    }
    
    @Override
    public Room updateRoom(String roomNumber, Room newRoom) throws RoomDNEException, UpdateRoomException {
        try {
            Room room = retrieveRoomByRoomNumber(roomNumber);
            if (roomNumber.equals(room.getRoomNumber())) {
                room.setRoomNumber(newRoom.getRoomNumber());
                room.setAvailable(newRoom.isAvailable());
                //room.setDisabled(newRoom.isDisabled()); 

                //room.setRoomType(newRoom.getRoomType()); // see how

                em.flush();

                return room;
            } else {
                throw new UpdateRoomException("Room number of room record to be updated does not match the existing Room record!");
            }
    
        } catch (NoResultException ex) {
            throw new RoomDNEException("Room Number " + roomNumber + " does not exist!");
        } 
    }
    
    @Override
    public void deleteRoom(String roomNumber) throws RoomDNEException {
        Room roomToRemove = retrieveRoomByRoomNumber(roomNumber);
        
        if (roomToRemove.isAvailable()) { //need to check if there are future reserved rooms???
            RoomType rt = roomToRemove.getRoomType();
            rt.getRooms().remove(roomToRemove);
            em.remove(roomToRemove);
        } else {
            RoomType rt = roomToRemove.getRoomType();
            rt.getRooms().remove(roomToRemove);
            roomToRemove.setDisabled(true);
        }
    }
}
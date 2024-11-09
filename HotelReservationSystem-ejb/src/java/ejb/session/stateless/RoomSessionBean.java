package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
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
    
    public Long createNewRoom(Room room, String roomTypeName) throws RoomExistsException, RoomTypeDNEException {
        try {
            // associate Room Rate to the room type
            RoomType rt = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(roomTypeName);
            rt.getRooms().add(room);
            em.persist(room);
            em.flush();
            return room.getRoomId();
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
    
    @Override
    public Room updateRoom(String roomNumber, Room newRoom) throws RoomDNEException, RoomExistsException {
        try {
            Room room = retrieveRoomByRoomNumber(roomNumber);
            
            room.setRoomNumber(newRoom.getRoomNumber());
            room.setAvailable(newRoom.isAvailable());
            room.setDisabled(newRoom.isDisabled());
            
            room.setRoomType(newRoom.getRoomType()); // see how
            
            em.flush();
            
            return room;
            
        } catch (NoResultException ex) {
            throw new RoomDNEException("Room Number " + roomNumber + " does not exist!");
        } catch (NonUniqueResultException ex) {
            throw new RoomExistsException("Room Number " + roomNumber + " already exists!");
        } 
    }
}
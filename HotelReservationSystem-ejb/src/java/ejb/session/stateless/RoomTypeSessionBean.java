package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.RoomTypeDNEException;
import util.exception.RoomTypeExistsException;

/**
 *
 * @author wallace
 */
@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {
    
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    public Long createNewRoomType(RoomType roomType) throws RoomTypeExistsException {
        try {
            em.persist(roomType);
            em.flush();
            return roomType.getRoomTypeId();
        } catch(PersistenceException ex) {
           if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new RoomTypeExistsException(); // to be confirmed
                } else {
                    throw new RoomTypeExistsException(ex.getMessage());
                }
            } else {
               throw new RoomTypeExistsException(ex.getMessage());
            }
        }
    }
    
    public List<RoomType> retrieveAllRoomTypes() {
        Query query = em.createQuery("SELECT r from RoomType r");
        return query.getResultList();
    }
    
    public RoomType retrieveRoomTypeByRoomTypeName(String roomTypeName) throws RoomTypeDNEException {
        Query query = em.createQuery("SELECT r from RoomType r WHERE r.roomTypeName = :inRoomTypeName");
        query.setParameter("inRoomTypeName", roomTypeName);
        
        
        try {
            RoomType rt = (RoomType) query.getSingleResult();
            rt.getRooms().size(); //lazy loaded data
            rt.getRoomRates().size();
            return rt;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomTypeDNEException("Room Type " + roomTypeName + " does not exist!");
        }
    }
    
    public RoomType updateRoomType(String roomTypeName, RoomType newRoomType) throws RoomTypeDNEException, RoomTypeExistsException {
        try {
            RoomType roomType = retrieveRoomTypeByRoomTypeName(roomTypeName);
            
            roomType.setRoomTypeName(newRoomType.getRoomTypeName());
            roomType.setDescription(newRoomType.getDescription());
            roomType.setRoomSize(newRoomType.getRoomSize());
            roomType.setBeds(newRoomType.getBeds());
            roomType.setCapacity(newRoomType.getCapacity());
            roomType.setAmenities(newRoomType.getAmenities());
            roomType.setDisabled(newRoomType.isDisabled());
            
            roomType.setRoomRates(newRoomType.getRoomRates()); // see how
            roomType.setRooms(newRoomType.getRooms()); // see how
            
            em.flush();
            
            return roomType;
            
        } catch (NoResultException ex) {
            throw new RoomTypeDNEException("Room Type " + roomTypeName + " does not exist!");
        } catch (NonUniqueResultException ex) {
            throw new RoomTypeExistsException("Room Type " + roomTypeName + " already exists!");
        } 
    }
}
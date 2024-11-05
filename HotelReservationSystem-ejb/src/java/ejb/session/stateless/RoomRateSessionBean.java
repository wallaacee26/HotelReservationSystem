package ejb.session.stateless;

import entity.RoomRate;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.RoomRateDNEException;
import util.exception.RoomRateExistsException;

/**
 *
 * @author wallace
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    public Long createNewRoomRate(RoomRate roomRate) throws RoomRateExistsException {
        try {
            em.persist(roomRate);
            em.flush();
            return roomRate.getRoomRateId();
        } catch(PersistenceException ex) {
           if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new RoomRateExistsException(); // to be confirmed
                } else {
                    throw new RoomRateExistsException(ex.getMessage());
                }
            } else {
               throw new RoomRateExistsException(ex.getMessage());
           }
        }
    }
    
    public List<RoomRate> retrieveAllRoomRates() {
        Query query = em.createQuery("SELECT r from RoomRate r");
        return query.getResultList();
    }
    
    public RoomRate retrieveRoomRateByRoomRateName(String roomRateName) throws RoomRateDNEException {
        Query query = em.createQuery("SELECT r from RoomRate r WHERE r.roomRateName = :roomRateName");
        query.setParameter("roomRateName", roomRateName);
        
        try {
            return (RoomRate) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomRateDNEException("Room Rate " + roomRateName + " does not exist!");
        }
    }
    
    public RoomRate updateRoomRate(String roomRateName, RoomRate newRoomRate) throws RoomRateDNEException, RoomRateExistsException {
        try {
            RoomRate roomRate = retrieveRoomRateByRoomRateName(roomRateName);
            
            roomRate.setRoomRateName(newRoomRate.getRoomRateName());
            roomRate.setRateType(newRoomRate.getRateType());
            roomRate.setRatePerNight(newRoomRate.getRatePerNight());
            roomRate.setStartDate(newRoomRate.getStartDate());
            roomRate.setEndDate(newRoomRate.getEndDate());
            roomRate.setDisabled(newRoomRate.isDisabled());
            
            roomRate.setRoomTypes(newRoomRate.getRoomTypes()); // see how
            
            em.flush();
            
            return roomRate;
            
        } catch (NoResultException ex) {
            throw new RoomRateDNEException("Room Rate " + roomRateName + " does not exist!");
        } catch (NonUniqueResultException ex) {
            throw new RoomRateExistsException("Room Rate " + roomRateName + " already exists!");
        } 
    }
    
    // TO CHECK ---------------------------------
    /*
    public void deleteRoomRate(String roomRateName) throws RoomRateDNEException {
        try {
            RoomRate roomRate = retrieveRoomRateByRoomRateName(roomRateName);

            // check if the room rate is linked with any room types (or reservations?)
            Query query = em.createQuery("SELECT COUNT(rt) FROM RoomType rt JOIN rt.roomRates rr WHERE rr = :roomRate");
            query.setParameter("roomRate", roomRate);
            Long count = (Long) query.getSingleResult();

            if (count > 0) {
                roomRate.setDisabled(true); // set as disabled, do not delete yet
            } else { // count == 0, not in use
                em.remove(roomRate);  // delete completely
            }
        } catch (RoomRateDNEException ex) {
            throw new RoomRateDNEException("Room Rate " + roomRateName + " does not exist!");
        }
    }
    */
}
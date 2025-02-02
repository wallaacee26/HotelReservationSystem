package ejb.session.stateless;

import entity.RoomType;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import util.exception.UpdateRoomTypeException;

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
                    throw new RoomTypeExistsException();
                } else {
                    throw new RoomTypeExistsException(ex.getMessage()); // UnknownPersistenceException, but just using this exception for simplicity
                }
            } else {
               throw new RoomTypeExistsException(ex.getMessage()); // UnknownPersistenceException, but just using this exception for simplicity
            }
        }
    }
    
    public List<RoomType> retrieveAllRoomTypes() {
        Query query = em.createQuery("SELECT r from RoomType r");
        List<RoomType> roomTypes = query.getResultList();
        for (RoomType roomType : roomTypes) {
            roomType.getReservedRooms().size();
            roomType.getRoomRates().size();
            roomType.getRooms().size();
        }
        return roomTypes;
    }
    
    public RoomType retrieveRoomTypeByRoomTypeName(String roomTypeName) throws RoomTypeDNEException {
        Query query = em.createQuery("SELECT r from RoomType r WHERE r.roomTypeName = :inRoomTypeName");
        query.setParameter("inRoomTypeName", roomTypeName);
        
        
        try {
            RoomType rt = (RoomType) query.getSingleResult();
            rt.getRooms().size(); //lazy loaded data
            rt.getRoomRates().size();
            rt.getReservedRooms().size();
            return rt;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomTypeDNEException("Room Type " + roomTypeName + " does not exist!");
        }
    }
    
    @Override
    public void setNextHigherRoomType(String currentTypeName, String nextHigherTypeName) {
        try {
            RoomType currentRoomType = retrieveRoomTypeByRoomTypeName(currentTypeName);
            RoomType nextHigherRoomType = retrieveRoomTypeByRoomTypeName(nextHigherTypeName);
            currentRoomType.setHigherRoomType(nextHigherRoomType);
        } catch (RoomTypeDNEException ex) {
            ex.printStackTrace();
        }
        
    }
    
    @Override
    public RoomType retrieveRoomTypeByRoomTypeId(Long roomTypeId) throws RoomTypeDNEException {
        RoomType roomType = em.find(RoomType.class, roomTypeId); 
        
        if (roomType != null) {
            roomType.getReservedRooms().size(); // trigger lazy fetching
            roomType.getRoomRates().size();
            roomType.getRooms().size();
            return roomType;
        } else {
            throw new RoomTypeDNEException("Room Type ID does not exist: " + roomTypeId);
        }
    }
    
    @Override
    public RoomType updateRoomType(String roomTypeName, RoomType newRoomType) throws RoomTypeDNEException, UpdateRoomTypeException {
        try {
            RoomType roomType = retrieveRoomTypeByRoomTypeName(roomTypeName);
            roomType.setRoomTypeName(newRoomType.getRoomTypeName());
            roomType.setDescription(newRoomType.getDescription());
            roomType.setRoomSize(newRoomType.getRoomSize());
            roomType.setBeds(newRoomType.getBeds());
            roomType.setCapacity(newRoomType.getCapacity());
            roomType.setAmenities(newRoomType.getAmenities());
            roomType.setDisabled(newRoomType.isDisabled());

            //roomType.setRoomRates(newRoomType.getRoomRates()); // see how
            //roomType.setRooms(newRoomType.getRooms()); // see how

            em.flush();

            return roomType;
        } catch (NoResultException ex) {
            throw new RoomTypeDNEException("Room Type " + roomTypeName + " does not exist!");
        } 
    }
    
    public void deleteRoomType(String roomTypeName) throws RoomTypeDNEException {
        RoomType rtToRemove = retrieveRoomTypeByRoomTypeName(roomTypeName);
        
        if (rtToRemove.getRooms().isEmpty() && rtToRemove.getRoomRates().isEmpty()) { // if no rooms and room rates are using this room type
            em.remove(rtToRemove);
        } else {
            rtToRemove.setDisabled(true);
        }
    }
    
    @Override
    public List<Integer> searchAvailableRoomTypesWithNumberOfRooms(LocalDate checkInDate, LocalDate checkOutDate) {
        
        int maxRoomTypeId = ((Long) em.createQuery("SELECT MAX(r.roomType.roomTypeId) FROM Room r").getSingleResult()).intValue();
        List<Integer> availableRoomsByRoomType = new ArrayList<>(Collections.nCopies(maxRoomTypeId + 1, 0));

        // get the total number of rooms with the associated roomtype (that are available and not disabled)
        Query roomCountQuery = em.createQuery(
            "SELECT r.roomType.roomTypeId, COUNT(r) FROM Room r "
                + "WHERE r.available = true AND r.disabled = false "
                + "GROUP BY r.roomType.roomTypeId");

        List<Object[]> roomCounts = roomCountQuery.getResultList();
        for (Object[] count : roomCounts) {
            Integer roomTypeId = ((Long) count[0]).intValue();
            Integer totalRooms = ((Long) count[1]).intValue();
            availableRoomsByRoomType.set(roomTypeId, totalRooms);
        }

        // get the number of reserved rooms with the associated roomtype
        Query reservedRoomQuery = em.createQuery(
            "SELECT rr.roomType.roomTypeId, COUNT(rr) FROM ReservedRoom rr "
                + "WHERE (rr.checkInDate < :inCheckOutDate) AND (rr.checkOutDate > :inCheckInDate) "
                + "GROUP BY rr.roomType.roomTypeId")
                .setParameter("inCheckInDate", checkInDate)
                .setParameter("inCheckOutDate", checkOutDate);

        List<Object[]> reservedRoomCounts = reservedRoomQuery.getResultList();
        for (Object[] count : reservedRoomCounts) {
            Integer roomTypeId = ((Long) count[0]).intValue();
            Integer reservedCount = ((Long) count[1]).intValue();
            availableRoomsByRoomType.set(roomTypeId, availableRoomsByRoomType.get(roomTypeId) - reservedCount);
        }

        return availableRoomsByRoomType; // index is roomTypeId, value is number of available rooms
    }
    
    @Override
    public boolean checkAvailabilityForRoomType(String roomTypeName, LocalDate checkInDate, LocalDate checkOutDate) throws RoomTypeDNEException {
        try {
            RoomType roomType = retrieveRoomTypeByRoomTypeName(roomTypeName);

            // count total available rooms of this RoomType that are not disabled
            Query availableRoomCountQuery = em.createQuery(
                "SELECT COUNT(r) FROM Room r " +
                "WHERE r.roomType = :roomType " +
                "AND r.available = true " +
                "AND r.disabled = false"
            );
            availableRoomCountQuery.setParameter("roomType", roomType);

            long totalAvailableRooms = (long) availableRoomCountQuery.getSingleResult();

            // count reserved rooms of this RoomType within the check-in and check-out dates
            Query reservedRoomCountQuery = em.createQuery(
                // combined effect of the 2 conditions : room is either currently booked before the input checkout date, or will be in use during input checkin date
                "SELECT COUNT(rr) FROM ReservedRoom rr "
                    + "WHERE rr.roomType = :roomType "
                    + "AND rr.checkInDate < :inCheckOutDate " // means room is still in use (its checkin date) before the input checkout date
                    + "AND rr.checkOutDate > :inCheckInDate") // means room is still in use (its checkout date) after the input checkin date
                    .setParameter("roomType", roomType)
                    .setParameter("inCheckInDate", checkInDate)
                    .setParameter("inCheckOutDate", checkOutDate);

            long reservedRoomCount = (long) reservedRoomCountQuery.getSingleResult();

            long finalAvailableRooms = totalAvailableRooms - reservedRoomCount;
            return finalAvailableRooms > 0; // returns true if there are available rooms, else return false
        } catch (RoomTypeDNEException ex) {
            throw new RoomTypeDNEException(ex.getMessage());
        }
    }
    
    @Override
    public List<Integer> searchAvailableRoomTypesWithNumberOfRoomsWebService(Date checkInDate, Date checkOutDate) {
        //convert date to localdate
        LocalDate checkInLocalDate = checkInDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate checkOutLocalDate = checkOutDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        int maxRoomTypeId = ((Long) em.createQuery("SELECT MAX(r.roomType.roomTypeId) FROM Room r").getSingleResult()).intValue();
        List<Integer> availableRoomsByRoomType = new ArrayList<>(Collections.nCopies(maxRoomTypeId + 1, 0));

        // get the total number of rooms with the associated roomtype (that are available and not disabled)
        Query roomCountQuery = em.createQuery(
            "SELECT r.roomType.roomTypeId, COUNT(r) FROM Room r "
                + "WHERE r.available = true AND r.disabled = false "
                + "GROUP BY r.roomType.roomTypeId");

        List<Object[]> roomCounts = roomCountQuery.getResultList();
        for (Object[] count : roomCounts) {
            Integer roomTypeId = ((Long) count[0]).intValue();
            Integer totalRooms = ((Long) count[1]).intValue();
            availableRoomsByRoomType.set(roomTypeId, totalRooms);
        }

        // get the number of reserved rooms with the associated roomtype
        Query reservedRoomQuery = em.createQuery(
            "SELECT rr.roomType.roomTypeId, COUNT(rr) FROM ReservedRoom rr "
                + "WHERE (rr.checkInDate < :inCheckOutDate) AND (rr.checkOutDate > :inCheckInDate) "
                + "GROUP BY rr.roomType.roomTypeId")
                .setParameter("inCheckInDate", checkInLocalDate)
                .setParameter("inCheckOutDate", checkOutLocalDate);

        List<Object[]> reservedRoomCounts = reservedRoomQuery.getResultList();
        for (Object[] count : reservedRoomCounts) {
            Integer roomTypeId = ((Long) count[0]).intValue();
            Integer reservedCount = ((Long) count[1]).intValue();
            availableRoomsByRoomType.set(roomTypeId, availableRoomsByRoomType.get(roomTypeId) - reservedCount);
        }

        return availableRoomsByRoomType; // index is roomTypeId, value is number of available rooms
    }
}
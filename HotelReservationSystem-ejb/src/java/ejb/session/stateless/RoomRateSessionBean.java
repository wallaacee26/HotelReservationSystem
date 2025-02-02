package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
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
import util.enumeration.RateTypeEnum;
import util.exception.RoomRateDNEException;
import util.exception.RoomRateExistsException;
import util.exception.RoomTypeDNEException;
import util.exception.RoomTypeDisabledException;

/**
 *
 * @author wallace
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;
    
    @Override
    public Long createNewRoomRate(RoomRate roomRate, String roomTypeName) throws RoomRateExistsException, RoomTypeDNEException, RoomTypeDisabledException {
        try {
            // associate Room Rate to the room type
            RoomType rt = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(roomTypeName);
            if (!rt.isDisabled()) {
                rt.getRoomRates().add(roomRate);
                em.persist(roomRate);
                em.flush();
                return roomRate.getRoomRateId();
            } else {
                throw new RoomTypeDisabledException("Room type: " + roomTypeName + " has been disabled! Unable to create a new Room Rate with this Room Type!");
            }
            
        } catch(PersistenceException ex) {
           if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                    throw new RoomRateExistsException(); // to be confirmed
                } else {
                    throw new RoomRateExistsException(ex.getMessage()); // UnknownPersistenceException, but just using this exception for simplicity
                }
            } else {
               throw new RoomRateExistsException(ex.getMessage()); // UnknownPersistenceException, but just using this exception for simplicity
           }
        } catch (RoomTypeDNEException ex) {
            throw new RoomTypeDNEException(ex.getMessage());
        }
    }
    
    @Override
    public List<RoomRate> retrieveAllRoomRates() {
        Query query = em.createQuery("SELECT r from RoomRate r");
        return query.getResultList();
    }
    
    @Override
    public RoomRate retrieveRoomRateByRoomRateName(String roomRateName) throws RoomRateDNEException {
        Query query = em.createQuery("SELECT r from RoomRate r WHERE r.roomRateName = :roomRateName");
        query.setParameter("roomRateName", roomRateName);
        
        try {
            return (RoomRate) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new RoomRateDNEException("Room Rate " + roomRateName + " does not exist!");
        }
    }
    
    @Override
    public RoomRate updateRoomRate(String roomRateName, RoomRate newRoomRate) throws RoomRateDNEException {
        try {
            RoomRate roomRate = retrieveRoomRateByRoomRateName(roomRateName);

            roomRate.setRoomRateName(newRoomRate.getRoomRateName());
            roomRate.setRateType(newRoomRate.getRateType());
            roomRate.setRatePerNight(newRoomRate.getRatePerNight());
            roomRate.setStartDate(newRoomRate.getStartDate());
            roomRate.setEndDate(newRoomRate.getEndDate());
            //roomRate.setDisabled(newRoomRate.isDisabled());
            
            // roomRate.setRoomTypes(newRoomRate.getRoomTypes()); // see how
            
            em.flush();
            
            return roomRate;
            
        } catch (NoResultException ex) {
            throw new RoomRateDNEException("Room Rate " + roomRateName + " does not exist!");
        } 
    }
    
    // TO CHECK ---------------------------------
    @Override
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
                // need to remove room rate entry from the associated room types?
                em.remove(roomRate);  // delete completely
                
            }
        } catch (RoomRateDNEException ex) {
            throw new RoomRateDNEException("Room Rate " + roomRateName + " does not exist!");
        }
    }
    
    @Override
    public BigDecimal calculateTotalRoomRateWithPublishedRate(String roomTypeName, LocalDate checkInDate, LocalDate checkOutDate) throws RoomTypeDNEException {
        try {
            BigDecimal totalRate = BigDecimal.ZERO;
            RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(roomTypeName);
            List<RoomRate> roomRates = roomType.getRoomRates();

            LocalDate date = checkInDate;
            // is not after the checkoutDate, meaning still during reservation (or booked) period
            while (!date.isEqual(checkOutDate)) {
                BigDecimal dailyRate = getPrevailingRoomRateForDateWithPublishedRate(roomRates, date);
                totalRate = totalRate.add(dailyRate);

                date = date.plusDays(1);
            }

            return totalRate;
        } catch (RoomTypeDNEException ex) {
            throw new RoomTypeDNEException(ex.getMessage());
        }
    }
    
    @Override
    public BigDecimal calculateTotalRoomRateWithNormalRate(String roomTypeName, LocalDate checkInDate, LocalDate checkOutDate) throws RoomTypeDNEException {
        try {
            BigDecimal totalRate = BigDecimal.ZERO;
            RoomType roomType = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName(roomTypeName);
            List<RoomRate> roomRates = roomType.getRoomRates();

            LocalDate date = checkInDate;
            // is not after the checkoutDate, meaning still during reservation (or booked) period
            while (!date.isEqual(checkOutDate)) {
                BigDecimal dailyRate = getPrevailingRoomRateForDateWithNormalRate(roomRates, date);
                totalRate = totalRate.add(dailyRate);

                date = date.plusDays(1);
            }

            return totalRate;
        } catch (RoomTypeDNEException ex) {
            throw new RoomTypeDNEException(ex.getMessage());
        }
    }

    // helper method for calculateTotalRoomRate()
    private BigDecimal getPrevailingRoomRateForDateWithPublishedRate(List<RoomRate> roomRates, LocalDate date) {
        RoomRate prevailingRate = roomRates.get(0); // set to the first one, then loop as usual
        Date actualDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()); // to convert from LocalDate to Date

        // rate precedence: promotion > peak > normal/published
        // loop through the rates to get the prevailing rate to charge the client
        for (RoomRate rate : roomRates) {
            if (!rate.isDisabled()) {
                // for promotion or peak
                if (isWithinDateRange(rate, actualDate)) {
                    
                    if (rate.getRateType().equals(RateTypeEnum.PROMOTION)) {
                        prevailingRate = rate;
                        break; // stop the moment we get a promotion rate (highest rate)
                    } else if (rate.getRateType().equals(RateTypeEnum.PEAK) && !prevailingRate.getRateType().equals(RateTypeEnum.PROMOTION)) {
                        prevailingRate = rate;
                    }
                    
                // for published
                } else if ((rate.getRateType().equals(RateTypeEnum.PUBLISHED)) 
                    && !prevailingRate.getRateType().equals(RateTypeEnum.PROMOTION) && !prevailingRate.getRateType().equals(RateTypeEnum.PEAK)) {
                        
                    prevailingRate = rate;
                        
                }
            }
        }

        return prevailingRate.getRatePerNight();
    }
    
        // helper method for calculateTotalRoomRate()
    private BigDecimal getPrevailingRoomRateForDateWithNormalRate(List<RoomRate> roomRates, LocalDate date) {
        RoomRate prevailingRate = roomRates.get(0); // set to the first one, then loop as usual
        Date actualDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()); // to convert from LocalDate to Date

        // rate precedence: promotion > peak > normal/published
        // loop through the rates to get the prevailing rate to charge the client
        for (RoomRate rate : roomRates) {
            if (!rate.isDisabled()) {
                // for promotion or peak
                if (isWithinDateRange(rate, actualDate)) {
                    
                    if (rate.getRateType().equals(RateTypeEnum.PROMOTION)) {
                        prevailingRate = rate;
                        break; // stop the moment we get a promotion rate (highest rate)
                    } else if (rate.getRateType().equals(RateTypeEnum.PEAK) && !prevailingRate.getRateType().equals(RateTypeEnum.PROMOTION)) {
                        prevailingRate = rate;
                    }
                    
                // for published
                } else if ((rate.getRateType().equals(RateTypeEnum.NORMAL)) 
                    && !prevailingRate.getRateType().equals(RateTypeEnum.PROMOTION) && !prevailingRate.getRateType().equals(RateTypeEnum.PEAK)) {
                        
                    prevailingRate = rate;
                }
            }
        }

        return prevailingRate.getRatePerNight();
    }

    // helper method for getPrevailingRoomRateForDate
    private boolean isWithinDateRange(RoomRate rate, Date date) {
        // within date range: if current date is not before rate's start date AND current date is not after rate's end date
        // used only for peak and promotion rates
        if (rate.getRateType().equals(RateTypeEnum.PROMOTION) || rate.getRateType().equals(RateTypeEnum.PEAK)) {
            return (!date.before(rate.getStartDate())) && !date.after(rate.getEndDate());
        }
        return false;
    }
}
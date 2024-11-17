package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.RateTypeEnum;

/**
 *
 * @author wallace
 */
@Entity
public class RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateId;

    @Column(nullable = false, unique = true)
    @NotNull
    @Size(min = 1, max = 32)
    private String roomRateName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private RateTypeEnum rateType;
    @Column(nullable = false)
    @DecimalMin("0.00")
    private BigDecimal ratePerNight;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true) // only for peak and promotion rates
    @FutureOrPresent
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = true) // only for peak and promotion rates
    @FutureOrPresent
    private Date endDate; 
    @Column(nullable = false)
    @NotNull
    private boolean disabled;

    // mappings:
    @ManyToMany(mappedBy = "roomRates")
    private List<RoomType> roomTypes;
    
    // default no-argument constructor for JPA
    public RoomRate() {
        this.roomTypes = new ArrayList<RoomType>();
    }

    public RoomRate(String roomRateName, RateTypeEnum rateType, BigDecimal ratePerNight, boolean disabled) {
        this();
        this.roomRateName = roomRateName;
        this.rateType = rateType;
        this.ratePerNight = ratePerNight;
        this.disabled = disabled;
    }
    
    public Long getRoomRateId() {
        return roomRateId;
    }

    public void setRoomRateId(Long roomRateId) {
        this.roomRateId = roomRateId;
    }

    public String getRoomRateName() {
        return roomRateName;
    }

    public void setRoomRateName(String roomRateName) {
        this.roomRateName = roomRateName;
    }

    public RateTypeEnum getRateType() {
        return rateType;
    }

    public void setRateType(RateTypeEnum rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public List<RoomType> getRoomTypes() {
        return roomTypes;
    }

    public void setRoomTypes(List<RoomType> roomTypes) {
        this.roomTypes = roomTypes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomRateId != null ? roomRateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the roomRateId fields are not set
        if (!(object instanceof RoomRate)) {
            return false;
        }
        RoomRate other = (RoomRate) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.roomRateName;
    }
    
}

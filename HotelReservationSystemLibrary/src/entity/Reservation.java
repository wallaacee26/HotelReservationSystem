package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 *
 * @author wallace
 */
@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    // mappings:
    @OneToMany(mappedBy = "reservation")
    private List<ReservedRoom> reservedRooms;
    
    @ManyToOne(optional = true)
    @JoinColumn(nullable = true) // might have a partner that reserve instead of guest
    private Guest guest;
    
    @ManyToOne(optional = true)
    @JoinColumn(nullable = true) // might have a guest that reserve instead of partner
    private Partner partner;
    
    // default no-argument constructor for JPA
    public Reservation() {
        this.reservedRooms = new ArrayList<>();
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public List<ReservedRoom> getReservedRooms() {
        return reservedRooms;
    }

    public void setReservedRooms(List<ReservedRoom> reservedRooms) {
        this.reservedRooms = reservedRooms;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Reservation[ id=" + reservationId + " ]";
    }
    
}

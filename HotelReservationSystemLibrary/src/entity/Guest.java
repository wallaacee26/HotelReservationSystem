package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 *
 * @author wallace
 */
@Entity
public class Guest extends Customer implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Column(nullable = false)
    private String phoneNumber; // uniquely identifiable?
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    
    // mappings:
    @OneToMany (mappedBy = "guest")
    private List<Reservation> reservations;
    
    // default no-argument constructor for JPA
    public Guest() {
        super();
        this.reservations = new ArrayList<Reservation>();
    }
    
    public Guest(String username, String password, String phoneNumber) {
        this();
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (customerId != null ? customerId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the guestId fields are not set
        if (!(object instanceof Guest)) {
            return false;
        }
        Guest other = (Guest) object;
        if ((this.customerId == null && other.customerId != null) || (this.customerId != null && !this.customerId.equals(other.customerId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Guest[ id=" + customerId + " ]";
    }
    
}

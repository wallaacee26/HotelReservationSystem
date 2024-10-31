package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author wallace
 */
@Entity
public class Staff implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long StaffId;

    // default no-argument constructor for JPA
    public Staff() {
        
    }
    
    public Long getStaffId() {
        return StaffId;
    }

    public void setStaffId(Long StaffId) {
        this.StaffId = StaffId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (StaffId != null ? StaffId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the StaffId fields are not set
        if (!(object instanceof Staff)) {
            return false;
        }
        Staff other = (Staff) object;
        if ((this.StaffId == null && other.StaffId != null) || (this.StaffId != null && !this.StaffId.equals(other.StaffId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Staff[ id=" + StaffId + " ]";
    }
    
}

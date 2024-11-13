package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import util.enumeration.AccessRightEnum;

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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AccessRightEnum accessRights;
    @Column(nullable = false, unique = true)
    @NotNull
    private String username;
    @NotNull
    @Column(nullable = false)
    private String password;

    // default no-argument constructor for JPA
    public Staff() {
        
    }
    
    public Staff(String username, String password, AccessRightEnum accessRights) {
        this();
        
        this.username = username;
        this.password = password;
        this.accessRights = accessRights;
    }

    // Getters and Setters //
    public AccessRightEnum getAccessRights() {
        return accessRights;
    }

    public void setAccessRights(AccessRightEnum accessRights) {
        this.accessRights = accessRights;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

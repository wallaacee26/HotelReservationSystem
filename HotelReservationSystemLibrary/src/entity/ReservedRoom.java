/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author yewkhang
 */
@Entity
public class ReservedRoom implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservedRoomId;

    @Column(nullable = false)
    private boolean isUpgraded;
    @Column(nullable = false)
    private Date checkInDate;
    
    // mappings:
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Room room; // the room used for booking
    
    @ManyToOne
    @JoinColumn(nullable = true) // optional
    private RoomType roomType; // similar to room
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Reservation reservation;

    public ReservedRoom() {
        
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }
    
    public Long getReservedRoomId() {
        return reservedRoomId;
    }

    public void setReservedRoomId(Long reservedRoomId) {
        this.reservedRoomId = reservedRoomId;
    }

    public boolean isIsUpgraded() {
        return isUpgraded;
    }

    public void setIsUpgraded(boolean isUpgraded) {
        this.isUpgraded = isUpgraded;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservedRoomId != null ? reservedRoomId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservedRoomId fields are not set
        if (!(object instanceof ReservedRoom)) {
            return false;
        }
        ReservedRoom other = (ReservedRoom) object;
        if ((this.reservedRoomId == null && other.reservedRoomId != null) || (this.reservedRoomId != null && !this.reservedRoomId.equals(other.reservedRoomId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ReservedRoom[ id=" + reservedRoomId + " ]";
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.ReservedRoomSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import entity.Reservation;
import entity.ReservedRoom;
import entity.Room;
import entity.Staff;
import java.util.List;
import java.util.Scanner;
import util.exception.ReservationDNEException;
import util.exception.RoomDNEException;
import util.exception.UpdateRoomException;

/**
 *
 * @author yewkhang
 */
public class RelationsModule {
    // reservation session bean
    private ReservationSessionBeanRemote reservationSessionBeanRemote;
    private ReservedRoomSessionBeanRemote reservedRoomSessionBeanRemote;
    private RoomSessionBeanRemote roomSessionBeanRemote;
    private Staff currentStaff;
    
    // constructor

    public RelationsModule(ReservedRoomSessionBeanRemote reservedRoomSessionBeanRemote, 
            RoomSessionBeanRemote roomSessionBeanRemote, 
            ReservationSessionBeanRemote reservationSessionBeanRemote, Staff currentStaff) {
        this.reservedRoomSessionBeanRemote = reservedRoomSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.currentStaff = currentStaff;
    }
    
    
    public void adminMenu() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            System.out.println("*** HoRS Management Client ***\n");
            System.out.println("You are logged in as " + currentStaff.getUsername() + " with " + currentStaff.getAccessRights().toString() + " rights");
            System.out.println("1: Walk-In Search Room");
            System.out.println("2: Check In Guest");
            System.out.println("3: Check Out Guest");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if (response == 1) {
                    // walk in search room -> reserve room
                    doWalkInSearchRoom();
                } else if (response == 2) {
                    // check in guest
                    doCheckInGuest();
                } else if (response == 3) {
                    // check out guest
                    doCheckOutGuest();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
            if (response == 4) { // logout
                break;
            }
        }
    }
    
    public void doWalkInSearchRoom() {
        
    }    
    
    public void doWalkInReserveRoom() {
        
    }
    
    public void doCheckInGuest() {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** HoRS Management Client :: Check In Guest ***\n");
        System.out.print("Enter Reservation ID> ");
        Long reservationId = sc.nextLong();
        // pass in reservationId, get the List of ReservedRooms
        try {
            Reservation reservation = reservationSessionBeanRemote.retrieveReservationByReservationId(reservationId);
            List<ReservedRoom> roomsToCheckIn = reservation.getReservedRooms();
            System.out.println("Rooms allocated: ");
            for (ReservedRoom r : roomsToCheckIn) {
                if(r.getRoom() != null) { // if a room is allocated
                    Room allocatedRoom = r.getRoom();
                    // make room not available for use
                    allocatedRoom.setAvailable(false);
                    // update room status
                    roomSessionBeanRemote.updateRoom(allocatedRoom.getRoomNumber(), allocatedRoom);
                    System.out.println("Room type: " + allocatedRoom.getRoomType().getRoomTypeName() + 
                            " allocated to Room Number " + allocatedRoom.getRoomNumber());
                } else { // no room is allocated
                    System.out.println("No room is allocated for " + r.getRoomType().getRoomTypeName());
                }
            }
        } catch (ReservationDNEException ex) {
            System.out.println("Error checking in reservation: " + ex.getMessage() + "!\n");
        } catch (RoomDNEException | UpdateRoomException ex) {
            System.out.println(ex.getMessage());
        }
        // for each reserved room, print out the room number, and set isAvailable to false
        // if the room allocated is null, print out "room not allocated"
        
        // need to check if can early check in?
    }
    
    public void doCheckOutGuest() {
        Scanner sc = new Scanner(System.in);
        String roomNumberCheckOut = "";
        
        System.out.println("*** HoRS Management Client :: Check Out Guest ***\n");
        System.out.print("Enter Room Number to Check Out> ");
        roomNumberCheckOut = sc.nextLine().trim();
        Room checkedOutRoom = new Room();
        checkedOutRoom.setRoomNumber(roomNumberCheckOut);
        checkedOutRoom.setAvailable(true);
        try {
            roomSessionBeanRemote.updateRoom(roomNumberCheckOut, checkedOutRoom);
            System.out.println("Room Number: " + roomNumberCheckOut + " successfully checked out!\n");
        } catch (RoomDNEException | UpdateRoomException ex) {
            System.out.println(ex.getMessage());
        }
        
        // pass in room number, set isAvailable to true
        // need to check if can late check out?
    }
}

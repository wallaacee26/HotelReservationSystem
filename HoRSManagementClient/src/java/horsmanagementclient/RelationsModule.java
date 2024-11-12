/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.ReservedRoomSessionBeanRemote;
import entity.Staff;
import java.util.Scanner;

/**
 *
 * @author yewkhang
 */
public class RelationsModule {
    // reservation session bean
    private ReservedRoomSessionBeanRemote reservedRoomSessionBeanRemote;
    private Staff currentStaff;
    
    // constructor
    
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
        // pass in reservationId, get the List of ReservedRooms
        // for each reserved room, print out the room number, and set isAvailable to false
        // if the room allocated is null, print out "room not allocated"
        
        // need to check if can early check in?
    }
    
    public void doCheckOutGuest() {
        // pass in room number, set isAvailable to true
        // need to check if can late check out?
    }
}

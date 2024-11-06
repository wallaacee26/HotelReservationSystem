/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.RoomType;
import entity.Staff;
import java.util.List;
import java.util.Scanner;
import util.exception.RoomTypeDNEException;
import util.exception.RoomTypeExistsException;

/**
 *
 * @author yewkhang
 */
public class OperationsModule {
    private RoomTypeSessionBeanRemote roomTypeSBRemote;
    private RoomSessionBeanRemote roomSBRemote;
    private Staff currentStaff;

    public OperationsModule() {
    }
    

    public OperationsModule(RoomTypeSessionBeanRemote roomTypeSBRemote, RoomSessionBeanRemote roomSBRemote, Staff currentStaff) {
        this.roomTypeSBRemote = roomTypeSBRemote;
        this.roomSBRemote = roomSBRemote;
        this.currentStaff = currentStaff;
    }
    
    public void adminMenu() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            System.out.println("*** HoRS Management Client ***\n");
            System.out.println("You are logged in as " + currentStaff.getUsername() + " with " + currentStaff.getAccessRights().toString() + " rights");
            System.out.println("1: Create New Room Type");
            System.out.println("2: View Room Type Details");
            System.out.println("3: View All Room Types");
            System.out.println("4: Create New Room");
            System.out.println("5: Update Room");
            System.out.println("6: Delete Room");
            System.out.println("7: View All Rooms");
            System.out.println("8: View Room Allocation Exception Report");
            System.out.println("9: Logout\n");
            response = 0;
            
            while(response < 1 || response > 9) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if (response == 1) {
                    // create new room type
                    doCreateNewRoomType();
                } else if (response == 2) {
                    // view room type details
                    doViewRoomTypeDetails();
                } else if (response == 3) {
                    // view all room types
                    doViewAllRoomTypes();
                } else if (response == 4) {
                    // view all partners
                } else if (response == 5) {
                    // view all partners
                } else if (response == 6) {
                    // view all partners
                } else if (response == 7) {
                    // view all partners
                } else if (response == 8) {
                    // view all partners
                } else if (response == 9) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
            if (response == 9) { // logout
                break;
            }
        }
    }
    
    private void doCreateNewRoomType() {
        Scanner sc = new Scanner(System.in);
        RoomType roomType = new RoomType();
        
        System.out.println("*** HoRS Management Client :: Create New Room Type ***\n");
        System.out.print("Enter name> ");
        roomType.setRoomTypeName(sc.nextLine().trim());
        System.out.print("Enter description> ");
        roomType.setDescription(sc.nextLine().trim());
        System.out.print("Enter size> ");
        roomType.setRoomSize(sc.nextDouble());
        System.out.print("Enter number of beds> ");
        roomType.setBeds(sc.nextInt());
        System.out.print("Enter capacity> ");
        roomType.setCapacity(sc.nextInt());
        sc.nextLine();
        System.out.print("Enter amenities> ");
        roomType.setAmenities(sc.nextLine().trim());
        
        try {
            Long newRoomTypeId = roomTypeSBRemote.createNewRoomType(roomType);
            System.out.println("New Room Type created: " + newRoomTypeId + "\n");
        } catch (RoomTypeExistsException ex) {
            System.out.println("Error when creating new room type. Room type already exists!\n");
        }
    }
    
    private void doViewRoomTypeDetails() {
        Scanner sc = new Scanner(System.in);
        String rtName;
        Integer response = 0;
        
        System.out.println("*** HoRS Management Client :: View Room Type Details ***\n");
        System.out.print("Enter Room Type Name> ");
        rtName = sc.nextLine().trim();
        try {
            RoomType roomType = roomTypeSBRemote.retrieveRoomTypeByRoomTypeName(rtName);
            System.out.println(":: Details for Room Type " + rtName + " ::");
            System.out.println("Name: " + roomType.getRoomTypeName());
            System.out.println("Description: " + roomType.getDescription());
            System.out.println("Size: " + roomType.getRoomSize());
            System.out.println("Number of beds: " + roomType.getBeds());
            System.out.println("Capacity: " + roomType.getCapacity());
            System.out.println("Amenities: " + roomType.getAmenities());
            while (true) {
                System.out.println("\nFurther actions: ");
                System.out.println("1: Update Room Type");
                System.out.println("2: Delete Room Type");
                System.out.println("3: Go back");
                response = 0;
                while(response < 1 || response > 3) {
                    System.out.print("> ");
                    response = sc.nextInt();
                    
                    if (response == 1) {
                        doUpdateRoomType();
                    } else if (response == 2) {
                        // delete room type
                        doDeleteRoomType();
                    } else if (response == 3) {
                        break;
                    } else {
                    System.out.println("Invalid option, please try again!\n");
                    }
                }
                if (response == 3) {
                    break;
                }
            }
        } catch (RoomTypeDNEException ex) {
            System.out.println("Error while viewing room type details. Room Type " + rtName + " does not exist!");
        }
        
    }
    
    private void doUpdateRoomType() { // under view room type details
        
    }
    
    private void doDeleteRoomType() { // under view room type details
        
    }
    
    private void doViewAllRoomTypes() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS Management Client :: View All Employees ***\n");
        
        List<RoomType> listOfRoomTypes = roomTypeSBRemote.retrieveAllRoomTypes();
        for (RoomType rt : listOfRoomTypes) {
            System.out.println("Name: " + rt.getRoomTypeName() +
                    " | Description: " + rt.getDescription() +
                    " | Room Size: " + rt.getRoomSize() +
                    " | Capacity: " + rt.getCapacity() +
                    " | Beds: " + rt.getBeds() + 
                    " | Amenities: " + rt.getAmenities() + 
                    " | isDisabled: " + rt.isDisabled());
        }
        
        System.out.print("Press any key to cotinue> ");
        sc.nextLine();
    }
}

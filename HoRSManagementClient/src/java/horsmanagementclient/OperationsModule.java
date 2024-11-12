/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.ReservedRoomSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import entity.Staff;
import java.util.List;
import java.util.Scanner;
import util.exception.RoomDNEException;
import util.exception.RoomExistsException;
import util.exception.RoomTypeDNEException;
import util.exception.RoomTypeDisabledException;
import util.exception.RoomTypeExistsException;
import util.exception.UpdateRoomException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author yewkhang
 */
public class OperationsModule {
    private RoomTypeSessionBeanRemote roomTypeSBRemote;
    private RoomSessionBeanRemote roomSBRemote;
    private ReservedRoomSessionBeanRemote reservedRoomSessionBeanRemote;
    private Staff currentStaff;

    public OperationsModule() {
    }
    

    public OperationsModule(RoomTypeSessionBeanRemote roomTypeSBRemote, RoomSessionBeanRemote roomSBRemote, 
            ReservedRoomSessionBeanRemote reservedRoomSessionBeanRemote, Staff currentStaff) {
        this.roomTypeSBRemote = roomTypeSBRemote;
        this.roomSBRemote = roomSBRemote;
        this.currentStaff = currentStaff;
        this.reservedRoomSessionBeanRemote = reservedRoomSessionBeanRemote;
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
                    // create new room
                    doCreateNewRoom();
                } else if (response == 5) {
                    // update room
                    doUpdateRoom();
                } else if (response == 6) {
                    // delete room
                    doDeleteRoom();
                } else if (response == 7) {
                    // view all rooms
                    doViewAllRooms();
                } else if (response == 8) {
                    // view room allocation exception report
                    doViewAllocationExceptionReport();
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
            // for testing
//            for (Room r : roomType.getRooms()) {
//                System.out.println(r.toString());
//            }
//            for (RoomRate rt : roomType.getRoomRates()) {
//                System.out.println(rt.toString());
//            }
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
            System.out.println("Error while viewing room type details. Room Type " + rtName + " does not exist!\n");
        }
        
    }
    
    private void doUpdateRoomType() { // under view room type details
        Scanner sc = new Scanner(System.in);
        RoomType newRoomType = new RoomType();
        String roomTypeName = "";
        
        System.out.println("*** HoRS Management Client :: Update Existing Room Type ***\n");
        System.out.print("Enter RoomType Name to Update> ");
        roomTypeName = sc.nextLine().trim();
        newRoomType.setRoomTypeName(roomTypeName);
        System.out.print("Enter Description> ");
        newRoomType.setDescription(sc.nextLine().trim());
        System.out.print("Enter size> ");
        newRoomType.setRoomSize(sc.nextDouble());
        System.out.print("Enter number of beds> ");
        newRoomType.setBeds(sc.nextInt());
        System.out.print("Enter capacity> ");
        newRoomType.setCapacity(sc.nextInt());
        sc.nextLine();
        System.out.print("Enter amenities> ");
        newRoomType.setAmenities(sc.nextLine().trim());
        
        try {
            roomTypeSBRemote.updateRoomType(roomTypeName, newRoomType);
            System.out.println("Room Type " + roomTypeName + " successfully updated!\n");
        } catch (RoomTypeDNEException | UpdateRoomTypeException ex) {
            System.out.println(ex.getMessage() + "\n");
        }
    }
    
    private void doDeleteRoomType() { // under view room type details
        Scanner sc = new Scanner(System.in);
        String roomTypeName = "";
        
        System.out.println("*** HoRS Management Client :: Delete Existing RoomType ***\n");
        System.out.print("Enter Name of RoomType to Delete> ");
        roomTypeName = sc.nextLine().trim();
        
        try {
            roomTypeSBRemote.deleteRoomType(roomTypeName);
            System.out.println("RoomType: " + roomTypeName + " successfully deleted!");
        } catch (RoomTypeDNEException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doViewAllRoomTypes() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS Management Client :: View All Room Types ***\n");
        
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
        
        System.out.print("Press ENTER to cotinue> ");
        sc.nextLine();
    }
    
    private void doCreateNewRoom() {
        Scanner sc = new Scanner(System.in);
        Room newRoom = new Room();
        String roomTypeName;
        String response = "";
        
        System.out.println("*** HoRS Management Client :: Create New Room ***\n");
        System.out.print("Enter Room Number> ");
        newRoom.setRoomNumber(sc.nextLine().trim());
        System.out.print("Enter Room Type> ");
        roomTypeName = sc.nextLine().trim();
        System.out.print("Enter Room Availability: y/n> ");
        response = sc.nextLine().trim();
        
        if (response.equals("y")) {
            newRoom.setAvailable(true);
        } else {
            newRoom.setAvailable(false);
        }     
        
        try {
            RoomType rt = roomTypeSBRemote.retrieveRoomTypeByRoomTypeName(roomTypeName);
            newRoom.setRoomType(rt);
            // already put into create new room SB
            // rt.getRooms().add(newRoom);
            try {
                Long newRoomId = roomSBRemote.createNewRoom(newRoom, rt.getRoomTypeName());
                System.out.println("New Room created: " + newRoomId + "\n");
            } catch (RoomExistsException ex) {
                System.out.println("Error when creating new room. Room already exists!\n");
            } catch (RoomTypeDisabledException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (RoomTypeDNEException ex) {
            System.out.println("Room Type " + roomTypeName + " does not exist!\n");
        } 
    }
    
    private void doUpdateRoom() {
        Scanner sc = new Scanner(System.in);
        Room newRoom = new Room();
        String roomNumber = "";
        
        System.out.println("*** HoRS Management Client :: Update Existing Room ***\n");
        System.out.print("Enter Room Number to Update> ");
        roomNumber = sc.nextLine().trim();
        newRoom.setRoomNumber(roomNumber);
        System.out.print("Enter Room Availability: y/n> ");
        String availability = sc.nextLine().trim();
        if (availability.equals("y")) {
            newRoom.setAvailable(true);
        } else {
            newRoom.setAvailable(false);
        }
        
        try {
            roomSBRemote.updateRoom(roomNumber, newRoom);
            System.out.println("Room " + roomNumber + " successfully updated!\n");
        } catch (RoomDNEException | UpdateRoomException ex) {
            System.out.println(ex.getMessage());
        } 
    }
    
    private void doDeleteRoom() {
        Scanner sc = new Scanner(System.in);
        String roomNumber = "";
        
        System.out.println("*** HoRS Management Client :: Delete Existing Room ***\n");
        System.out.print("Enter Room Number to Delete> ");
        roomNumber = sc.nextLine().trim();
        try {
            roomSBRemote.deleteRoom(roomNumber);
            System.out.println("Room Number " + roomNumber + " successfully removed!");
        } catch (RoomDNEException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doViewAllRooms() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS Management Client :: View All Rooms ***\n");
        
        List<Room> listOfRooms = roomSBRemote.retrieveAllRooms();
        for (Room r : listOfRooms) {
            System.out.println("Room Number: " + r.getRoomNumber() +
                    " | isAvailable: " + r.isAvailable() + 
                    " | isDisabled: " + r.isDisabled() + 
                    " | Room Type: " + r.getRoomType().getRoomTypeName());
        }
        
        System.out.print("Press any key to cotinue> ");
        sc.nextLine();
    }
    
    private void doViewAllocationExceptionReport() {
        
    }
}

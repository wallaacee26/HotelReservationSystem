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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
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
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public OperationsModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    

    public OperationsModule(RoomTypeSessionBeanRemote roomTypeSBRemote, RoomSessionBeanRemote roomSBRemote, 
            ReservedRoomSessionBeanRemote reservedRoomSessionBeanRemote, Staff currentStaff) {
        this();
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
            System.out.println("9: Logout");
            System.out.println("10: Force Room Allocation\n");
            response = 0;
            
            while(response < 1 || response > 10) {
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
                } else if (response == 10) {
                    doForceAllocation();
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
        RoomType newRoomType = new RoomType();
        
        
        System.out.println("*** HoRS Management Client :: Create New Room Type ***\n");
        System.out.print("Enter name> ");
        newRoomType.setRoomTypeName(sc.nextLine().trim());
        System.out.print("Enter description> ");
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
        
        List<RoomType> listOfRoomTypes = roomTypeSBRemote.retrieveAllRoomTypes();
        while(true) {
            System.out.print("Select Next Higher Tier Room Type: \n");
            int totalRoomTypes;
            for (totalRoomTypes = 0; totalRoomTypes < listOfRoomTypes.size(); totalRoomTypes++) {
                System.out.println((totalRoomTypes+1) + ": " + listOfRoomTypes.get(totalRoomTypes).getRoomTypeName());
            }
            System.out.println((totalRoomTypes+1) + ": None");
            System.out.println("");
            System.out.print("> ");
            int roomTypeNumber = sc.nextInt();
            
            if (roomTypeNumber >= 1 && roomTypeNumber <= listOfRoomTypes.size() + 1) { //if within range
                if (roomTypeNumber != totalRoomTypes + 1) { 
                    newRoomType.setHigherRoomType(listOfRoomTypes.get(roomTypeNumber - 1));
                } else { // selected "None" higher room type
                    newRoomType.setHigherRoomType(null); 
                }
                break;
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        Set<ConstraintViolation<RoomType>> violations = validator.validate(newRoomType);
        if (violations.isEmpty()) {
            try {
                Long newRoomTypeId = roomTypeSBRemote.createNewRoomType(newRoomType);
                System.out.println("New Room Type created: " + newRoomTypeId + "\n");
            } catch (RoomTypeExistsException ex) {
                System.out.println("Error when creating new room type. Room type already exists!\n");
            }
        } else {
            showValidationErrorsForRoomType(violations);
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
//            for (Room r : newRoomType.getRooms()) {
//                System.out.println(r.toString());
//            }
//            for (RoomRate rt : newRoomType.getRoomRates()) {
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
                        doUpdateRoomType(rtName);
                    } else if (response == 2) {
                        // delete room type
                        doDeleteRoomType(rtName);
                        return; // go back to options
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
    
    private void doUpdateRoomType(String oldRoomTypename) { // under view room type details
        Scanner sc = new Scanner(System.in);
        RoomType newRoomType = new RoomType();
        String roomTypeName = "";
        
        System.out.println("*** HoRS Management Client :: Update Existing Room Type ***\n");
        System.out.print("Enter new Room Type Name> ");
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
        
        Set<ConstraintViolation<RoomType>> violations = validator.validate(newRoomType);
        if (violations.isEmpty()) {
            try {
                roomTypeSBRemote.updateRoomType(oldRoomTypename, newRoomType);
                System.out.println("Room Type " + oldRoomTypename + " successfully updated!\n");
            } catch (RoomTypeDNEException | UpdateRoomTypeException ex) {
                System.out.println(ex.getMessage() + "\n");
            }
        } else {
            showValidationErrorsForRoomType(violations);
        }  
    }
    
    private void doDeleteRoomType(String oldRoomTypename) { // under view room type details
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** HoRS Management Client :: Delete Existing RoomType ***\n");
        System.out.println("Deleting Room Type " + oldRoomTypename);
        
        try {
            roomTypeSBRemote.deleteRoomType(oldRoomTypename);
            System.out.println("RoomType: " + oldRoomTypename + " successfully deleted!");
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
        
        System.out.print("Press ENTER to continue> ");
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
        newRoom.setDisabled(false); // need?
        
        Set<ConstraintViolation<Room>> violations = validator.validate(newRoom);
        if (violations.isEmpty()) {
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
        } else {
            showValidationErrorsForRoom(violations);
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
        
        Set<ConstraintViolation<Room>> violations = validator.validate(newRoom);
        if (violations.isEmpty()) {
            try {
                roomSBRemote.updateRoom(roomNumber, newRoom);
                System.out.println("Room " + roomNumber + " successfully updated!\n");
            } catch (RoomDNEException | UpdateRoomException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            showValidationErrorsForRoom(violations);
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
        
        System.out.print("Press any key to continue> ");
        sc.nextLine();
    }
    
    private void doViewAllocationExceptionReport() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS Management Client :: View Room Allocation Exception Report ***\n");
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
        System.out.print("Date for allocation exceptions (dd/MM/YY): ");
        try {
            Date startDate = inputDateFormat.parse(sc.nextLine().trim());
            LocalDate date = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String report = reservedRoomSessionBeanRemote.generateExceptionReport(date);
            System.out.println(report);
            System.out.print("Press ENTER key to continue> ");
            sc.nextLine();
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void doForceAllocation() {
        Scanner sc = new Scanner(System.in);
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
        System.out.print("Date for allocation (dd/MM/YY): ");
        try {
            Date startDate = inputDateFormat.parse(sc.nextLine().trim());
            LocalDate date = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            reservedRoomSessionBeanRemote.allocateRoomsForDate(date);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void showValidationErrorsForRoomType(Set<ConstraintViolation<RoomType>> violations) {
        System.out.println("\n Input data validation error!");
        
        for (ConstraintViolation violation : violations) {
            System.out.println("\t" + violation.getPropertyPath() + "-" + violation.getInvalidValue() + "; " + violation.getMessage());
        }
        System.out.println("\nPlease try again!");
    }
    
    private void showValidationErrorsForRoom(Set<ConstraintViolation<Room>> violations) {
        System.out.println("\n Input data validation error!");
        
        for (ConstraintViolation violation : violations) {
            System.out.println("\t" + violation.getPropertyPath() + "-" + violation.getInvalidValue() + "; " + violation.getMessage());
        }
        System.out.println("\nPlease try again!");
    }
}

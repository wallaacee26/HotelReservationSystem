/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.ReservedRoomSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Reservation;
import entity.ReservedRoom;
import entity.Room;
import entity.RoomType;
import entity.Staff;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.ReservationDNEException;
import util.exception.ReservationExistsException;
import util.exception.RoomDNEException;
import util.exception.RoomTypeDNEException;
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
    private RoomTypeSessionBeanRemote roomTypeSBRemote;
    private RoomRateSessionBeanRemote roomRateSBRemote;
    private Staff currentStaff;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    // constructor

    public RelationsModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public RelationsModule(ReservedRoomSessionBeanRemote reservedRoomSessionBeanRemote, 
            RoomSessionBeanRemote roomSessionBeanRemote, 
            ReservationSessionBeanRemote reservationSessionBeanRemote, 
            RoomTypeSessionBeanRemote roomTypeSBRemote,
            RoomRateSessionBeanRemote roomRateSBRemote, Staff currentStaff) {
        this();
        this.reservedRoomSessionBeanRemote = reservedRoomSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.roomTypeSBRemote = roomTypeSBRemote;
        this.roomRateSBRemote = roomRateSBRemote;
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
    
    public void doWalkInSearchRoom() { // leads to reservation
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** HoRS Management Client :: Search Hotel Rooms ***\n");
        System.out.print("Enter Check-In Date (Format: DD/MM/YYYY)> ");
        String[] checkInInput = sc.nextLine().split("/");
        System.out.print("Enter Check-Out Date (Format: DD/MM/YYYY)> ");
        String[] checkOutInput = sc.nextLine().split("/");
        System.out.println();
        
        if (checkInInput.length != 3 || checkOutInput.length != 3) {
            System.out.println("Invalid date input(s)! Please try again.");
            return;
        }
        
        try {
            LocalDate checkInDate = LocalDate.of(Integer.parseInt(checkInInput[2]), Integer.parseInt(checkInInput[1]), Integer.parseInt(checkInInput[0]));
            LocalDate checkOutDate = LocalDate.of(Integer.parseInt(checkOutInput[2]), Integer.parseInt(checkOutInput[1]), Integer.parseInt(checkOutInput[0]));
            
            List<Integer> listOfAllRoomTypes = roomTypeSBRemote.searchAvailableRoomTypesWithNumberOfRooms(checkInDate, checkOutDate);
            
            List<RoomType> availableRoomTypes = new ArrayList<>();
            List<Integer> availableRoomsPerRoomType = new ArrayList<>();
            
            for (int i = 0; i < listOfAllRoomTypes.size(); i++) {
                if (listOfAllRoomTypes.get(i) > 0) { // i == 0 will never be > 0, so never triggered
                    availableRoomTypes.add(roomTypeSBRemote.retrieveRoomTypeByRoomTypeId((long) i));
                    availableRoomsPerRoomType.add(listOfAllRoomTypes.get(i));
                }
            }
            
            if (availableRoomTypes.size() == 0) {
                System.out.println("There are no available rooms!");
            } else {
                System.out.println("Available Room Types:");
                for (int i = 1; i <= availableRoomTypes.size(); i++) {
                    String roomTypeName = availableRoomTypes.get(i - 1).getRoomTypeName();
                    int numberOfAvailableRooms = availableRoomsPerRoomType.get(i - 1);
                    // int numberOfAvailableRooms = roomTypeSBRemote.findNumberOfAvailableRoomsForRoomType(roomTypeName, checkInDate, checkOutDate);
                    System.out.println(i + ": " + roomTypeName + " | Number Of Available Rooms: " + numberOfAvailableRooms + " | Reservation Amount: $" + roomRateSBRemote.calculateTotalRoomRateWithPublishedRate(roomTypeName, checkInDate, checkOutDate));
                }
            }
            System.out.println();
            
            int response = 1;
            while(response == 1) {
                System.out.println("------------------------");
                System.out.println("1: Reserve Hotel Room");
                System.out.println("2: Back\n");
                System.out.print("> ");
                response = sc.nextInt();

                if (response == 1) {
                    // reserve a hotel
                    doWalkInReserveHotelRoom(checkInDate, checkOutDate);
                    break; // dont repeat same UI
                    
                } else if (response == 2) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
        } catch (DateTimeException ex) {
            System.out.println("Invalid date input(s)! Please try again.");
        } catch (RoomTypeDNEException ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ReservationExistsException ex) {
            System.out.println("Reservation already exists!"); // will likely never come here anyways
        } catch (ReservationDNEException ex) {
            System.out.println("Reservation does not exist!"); // will likely never come here anyways
        }
    }    
    
    public void doWalkInReserveHotelRoom(LocalDate checkInDate, LocalDate checkOutDate) throws RoomTypeDNEException, ReservationExistsException, ReservationDNEException {
        try {
            Scanner sc = new Scanner(System.in);
            Reservation reservation = new Reservation();
            Long reservationId = (long) -1;
            BigDecimal totalBookingAmount = BigDecimal.ZERO; // simply for initialisation
            reservation.setBookingPrice(totalBookingAmount); // simply for initialisation
               
//          // guest - reservation association
//          reservation.setGuest(currentGuest);
            boolean hasReservationBeenCreated = false;
                
            String response = "Y";
            while (response.equals("Y")) {
                System.out.println("*** HoRS Reservation Client :: Reserve Hotel Room ***\n");
                System.out.print("Enter Room Type to reserve (e.g. Deluxe Room)> ");
                String roomTypeName = "";
                roomTypeName = sc.nextLine().trim();
                System.out.println("");
                
                boolean inputFlag = false;
                while (inputFlag == false) {
                    while (roomTypeName.length() == 0) {
                        System.out.print("No input detected. Please enter a room type> ");
                        roomTypeName = sc.nextLine().trim();
                        System.out.println();
                    }

                    List<RoomType> allRoomTypes = roomTypeSBRemote.retrieveAllRoomTypes();
                    boolean isValid = false;
                    while (!isValid) {
                        for (RoomType roomType : allRoomTypes) {
                            if (roomTypeName.equals(roomType.getRoomTypeName())) {
                                isValid = true;
                                break;
                            }
                        }
                        if (roomTypeName.equals("exit")) {
                            if (!hasReservationBeenCreated) {
                                System.out.println("You did not reserve a room!");
                            } else { // if created, print out reservation ID
                                System.out.println("Successful reservation made! Your Reservation ID is: " + reservationId + "!");
                            }
                            System.out.println();
                            return;
                        }
                        if (!isValid) {
                            // if input room type is not valid, then prompt for re-input
                            System.out.print("Error in getting room type, please enter a valid room type. (Type 'exit' to stop reserving more rooms)> ");
                            roomTypeName = sc.nextLine().trim();
                            System.out.println();
                        }
                    }

                    // if available, return true (move on), else if not available, this method returns false and returns
                    if (!roomTypeSBRemote.checkAvailabilityForRoomType(roomTypeName, checkInDate, checkOutDate)) {
                        System.out.print("There are no available rooms for this Room Type! Please try again> ");
                        roomTypeName = sc.nextLine().trim();
                    } else {
                        break;
                    }
                }
                
                RoomType roomType = roomTypeSBRemote.retrieveRoomTypeByRoomTypeName(roomTypeName);
                
                // creates the reservation if not already created before (to link reserved rooms to the same reservation object)
                if (!hasReservationBeenCreated) {
                    reservationId = reservationSessionBeanRemote.createNewReservation(reservation); // create new reservation first
                    reservation = reservationSessionBeanRemote.retrieveReservationByReservationId(reservationId); // get back a managed instance of reservation
                    //currentGuest.getReservations().add(reservation);
                    hasReservationBeenCreated = true;
                }

                ReservedRoom reservedRoom = new ReservedRoom();
                reservedRoom.setCheckInDate(checkInDate);
                reservedRoom.setCheckOutDate(checkOutDate);
                reservedRoom.setIsUpgraded(false); // initially not upgraded
                
                // check bean validations
                Set<ConstraintViolation<ReservedRoom>> violations = validator.validate(reservedRoom);
                if (violations.isEmpty()) {
                    reservedRoomSessionBeanRemote.createNewReservedRoom(reservedRoom, reservationId, roomType.getRoomTypeId());
                    LocalDate today = LocalDate.now();
                    if (checkInDate.isEqual(today)) { // force allocation if the room is reserved for today's checkin
                        reservedRoomSessionBeanRemote.allocateRoomsForDate(checkInDate);
                    }
                } else {
                    showValidationErrorsForReservedRoom(violations);
                }
                
                totalBookingAmount = totalBookingAmount.add(roomRateSBRemote.calculateTotalRoomRateWithNormalRate(roomTypeName, checkInDate, checkOutDate));
                reservationSessionBeanRemote.updateReservationBookingAmount(reservationId, totalBookingAmount);
                
                System.out.print("A " + roomTypeName + " has successfully been reserved! Would you like to reserve more hotel rooms? (Y/N)> ");
                response = sc.nextLine().trim();
                System.out.println("");
                
                if (!response.equals("Y")) {
                    System.out.println("Successful reservation made! Reservation ID: " + reservationId);
                    break;
                }
            }
 
        } catch (RoomTypeDNEException ex) {
            throw new RoomTypeDNEException(ex.getMessage());
        } catch (ReservationExistsException ex) {
            throw new ReservationExistsException(ex.getMessage());
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException(ex.getMessage());
        }
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
    
    private void showValidationErrorsForReservedRoom(Set<ConstraintViolation<ReservedRoom>> violations) {
        System.out.println("\n Input data validation error!");
        
        for (ConstraintViolation violation : violations) {
            System.out.println("\t" + violation.getPropertyPath() + "-" + violation.getInvalidValue() + "; " + violation.getMessage());
        }
        System.out.println("\nPlease try again!");
    }
}

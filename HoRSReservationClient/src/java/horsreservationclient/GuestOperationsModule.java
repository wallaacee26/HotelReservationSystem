/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsreservationclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.ReservedRoomSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Guest;
import entity.Reservation;
import entity.ReservedRoom;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.GuestDNEException;
import util.exception.ReservationDNEException;
import util.exception.ReservationExistsException;
import util.exception.RoomTypeDNEException;
/**
 *
 * @author wallace
 */
public class GuestOperationsModule {
    
    private Guest currentGuest;
    private GuestSessionBeanRemote guestSBRemote;
    private ReservationSessionBeanRemote reservationSBRemote;
    private RoomTypeSessionBeanRemote roomTypeSBRemote;
    private RoomRateSessionBeanRemote roomRateSBRemote;
    private ReservedRoomSessionBeanRemote reservedRoomSBRemote;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public GuestOperationsModule() {
        this.validatorFactory = Validation.buildDefaultValidatorFactory();
        this.validator = validatorFactory.getValidator();
    }
    

    public GuestOperationsModule(GuestSessionBeanRemote guestSBRemote, ReservationSessionBeanRemote reservationSBRemote,
            RoomTypeSessionBeanRemote roomTypeSBRemote, RoomRateSessionBeanRemote roomRateSBRemote,
            ReservedRoomSessionBeanRemote reservedRoomSBRemote, Guest currentGuest) {
        this();
        this.guestSBRemote = guestSBRemote;
        this.reservationSBRemote = reservationSBRemote;
        this.roomTypeSBRemote = roomTypeSBRemote;
        this.roomRateSBRemote = roomRateSBRemote;
        this.reservedRoomSBRemote = reservedRoomSBRemote;
        this.currentGuest = currentGuest;
    }
    
    public void guestMenu() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            System.out.println("*** HoRS Reservation Client ***\n");
            System.out.println("Welcome! You are logged in as " + currentGuest.getEmail() + "!");
            System.out.println("1: Search Hotel Rooms");
            System.out.println("2: View My Reservation Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Logout");
            response = 0;
            
            while(response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if (response == 1) {
                    // search hotel rooms
                    doSearchHotelRoomAsGuest();
                    
                } else if (response == 2) {
                    // view my reservation details for specific reservation
                    System.out.print("Enter your Reservation ID> ");
                    try {
                        Long reservationId = sc.nextLong();
                        sc.nextLine();
                        doViewAReservation(reservationId);
                        
                    } catch (InputMismatchException ex) {
                        System.out.println("Invalid Reservation ID, please try again!");
                    }
                    
                } else if (response == 3) {
                    // view all my reservations (list)
                    doViewAllMyReservations();
                    
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
    
    private void doSearchHotelRoomAsGuest() { // can reserve
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** HoRS Reservation Client :: Search Hotel Rooms ***\n");
        boolean inputDatesValidated = false;
        String[] checkInInput;
        String[] checkOutInput;
        LocalDate checkInDate = LocalDate.now(); // only for initialisation
        LocalDate checkOutDate = LocalDate.now(); // only for initialisation
        
        while(!inputDatesValidated) {
            System.out.print("Enter Check-In Date (Format: DD/MM/YYYY)> ");
            checkInInput = sc.nextLine().split("/");
            System.out.print("Enter Check-Out Date (Format: DD/MM/YYYY)> ");
            checkOutInput = sc.nextLine().split("/");
            System.out.println();

            if (checkInInput.length != 3 || checkOutInput.length != 3) { // first check: for invalid date format
                System.out.println("Invalid date input(s)! Please try again.");
            } else {
                checkInDate = LocalDate.of(Integer.parseInt(checkInInput[2]), Integer.parseInt(checkInInput[1]), Integer.parseInt(checkInInput[0]));
                checkOutDate = LocalDate.of(Integer.parseInt(checkOutInput[2]), Integer.parseInt(checkOutInput[1]), Integer.parseInt(checkOutInput[0]));

                if (!checkOutDate.isAfter(checkInDate)) { // second check: for making sure check-out date is later than check-in date
                    System.out.println("Check-out date must be after check-in date! Please try again.");
                } else {
                    
                    if (checkInDate.isBefore(LocalDate.now())) {
                        System.out.println("Check-in date cannot be before today (" + LocalDate.now() + ")! Please try again.");
                    } else {
                        inputDatesValidated = true;
                    }
                }
            }
        }
        
        try {
            
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
                System.out.println();
                return;
            } else {
                System.out.println("Available Room Types:");
                for (int i = 1; i <= availableRoomTypes.size(); i++) {
                    String roomTypeName = availableRoomTypes.get(i - 1).getRoomTypeName();
                    int numberOfAvailableRooms = availableRoomsPerRoomType.get(i - 1);
                    // int numberOfAvailableRooms = roomTypeSBRemote.findNumberOfAvailableRoomsForRoomType(roomTypeName, checkInDate, checkOutDate);
                    System.out.println(i + ": " + roomTypeName + " | Number Of Available Rooms: " + numberOfAvailableRooms + " | Reservation Amount (for duration): $" + roomRateSBRemote.calculateTotalRoomRateWithNormalRate(roomTypeName, checkInDate, checkOutDate));
                }
            }
            
            int response = 1;
            while(response == 1) {
                System.out.println("------------------------");
                System.out.println("1: Reserve Hotel Room");
                System.out.println("2: Back\n");
                System.out.print("> ");
                response = sc.nextInt();

                if (response == 1) {
                    // reserve a hotel
                    doReserveHotelRoom(checkInDate, checkOutDate);
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
        } catch (GuestDNEException ex) {
            System.out.println("Guest does not exist!"); // will likely never come here anyways
        }
    }
    
    private void doReserveHotelRoom(LocalDate checkInDate, LocalDate checkOutDate)
            throws RoomTypeDNEException, ReservationExistsException, ReservationDNEException, GuestDNEException {
        try {
            Scanner sc = new Scanner(System.in);
            Reservation reservation = new Reservation();
            Long reservationId = (long) -1; // simply for initialisation
            BigDecimal totalBookingAmount = BigDecimal.ZERO; // simply for initialisation
            reservation.setBookingPrice(totalBookingAmount); // simply for initialisation
               
            // guest - reservation association
            reservation.setCustomerOrGuest(currentGuest);
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
                                System.out.println("Your Reservation ID is: " + reservationId + "!");
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
                // will only reach this stage when roomType is valid, so it is safe to link reservationId here
                if (!hasReservationBeenCreated) {
                    Set<ConstraintViolation<Reservation>> violations = validator.validate(reservation);
                    if (violations.isEmpty()) {
                        try {
                            reservationId = reservationSBRemote.createNewReservation(reservation); // create new reservation first
                            reservationSBRemote.associateReservationWithGuest(reservationId, currentGuest.getCustomerId());
                            hasReservationBeenCreated = true;
                        } catch (ReservationExistsException ex) {
                            System.out.println("An error occurred creating a new reservation!\n");
                        }
                    } else {
                        showValidationErrorsForReservation(violations);
                    }   
                }

                ReservedRoom reservedRoom = new ReservedRoom();
                reservedRoom.setCheckInDate(checkInDate);
                reservedRoom.setCheckOutDate(checkOutDate);
                reservedRoom.setIsUpgraded(false); // initially not upgraded
                
                
                Set<ConstraintViolation<ReservedRoom>> violations = validator.validate(reservedRoom);
                if (violations.isEmpty()) {
                    try {
                        reservedRoomSBRemote.createNewReservedRoom(reservedRoom, reservationId, roomType.getRoomTypeId());
                    } catch (ReservationDNEException ex) {
                        System.out.println("An error occurred creating a new reservation: " + ex.getMessage() + "!\n");
                    } catch (RoomTypeDNEException ex) {
                        System.out.println("An error occurred creating a new reservation: " + ex.getMessage() + "!\n");
                    } 
                } else {
                    showValidationErrorsForReservedRoom(violations);
                }   
                
                LocalDate today = LocalDate.now();
                if (checkInDate.isEqual(today)) { // force allocation if the room is reserved for today's checkin
                    reservedRoomSBRemote.allocateRooms();
                }
                                
                totalBookingAmount = totalBookingAmount.add(roomRateSBRemote.calculateTotalRoomRateWithNormalRate(roomTypeName, checkInDate, checkOutDate));
                reservationSBRemote.updateReservationBookingAmount(reservationId, totalBookingAmount); // no need for validation, done on server-side calculations via method
                
                System.out.print("A " + roomTypeName + " has successfully been reserved! Would you like to reserve more hotel rooms? (Y/N)> ");
                response = sc.nextLine().trim();
                System.out.println("");
                
                if (!response.equals("Y")) {
                    System.out.println("Successful reservation made! Your Reservation ID is: " + reservationId + "!");
                    break;
                }
            }
 
        } catch (RoomTypeDNEException ex) {
            throw new RoomTypeDNEException(ex.getMessage());
        } catch (ReservationDNEException ex) {
            throw new ReservationDNEException(ex.getMessage());
        } catch (GuestDNEException ex) {
            throw new GuestDNEException(ex.getMessage());
        }
    }
    
    
    
    
    private void doViewAReservation(Long reservationId) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS Reservation Client :: View My Reservation Details ***\n");
        
        try {
            Reservation reservation = reservationSBRemote.retrieveReservationByReservationId(reservationId);
            System.out.println(
                "Reservation ID: " + reservation.getReservationId() +
                " | Number Of Reserved Rooms: " + reservation.getReservedRooms().size() +
                " | Check-In Date: " + reservation.getReservedRooms().get(0).getCheckInDate().toString() +
                " | Check-Out Date: " + reservation.getReservedRooms().get(0).getCheckOutDate().toString() +
                " | Reservation Amount: $" + reservation.getBookingPrice()
            );
            boolean hasReservedRoom = false;
            for (ReservedRoom reservedRoom : reservation.getReservedRooms()) {
                if (reservedRoom.getRoom() != null) {
                    System.out.print("Room " + reservedRoom.getRoom().getRoomNumber() + " | ");
                    hasReservedRoom = true;
                }
            }
            if (hasReservedRoom) {
                System.out.println();
            }
            System.out.println("");
        } catch (ReservationDNEException ex) {
            System.out.println("Error viewing reservation: " + ex.getMessage() + "!\n");
        }
        
        System.out.print("Press ENTER to continue> ");
        sc.nextLine();
    }
    
    private void doViewAllMyReservations() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS Reservation Client :: View All My Reservations ***\n");
        
        try {
            List<Reservation> listOfReservations = reservationSBRemote.retrieveAllReservationsOfGuestId(currentGuest.getCustomerId());
            if (listOfReservations.size() > 0) {
                for (Reservation reservation : listOfReservations) {
                    System.out.println(
                        "Reservation ID: " + reservation.getReservationId() +
                        " | Number Of Reserved Rooms: " + reservation.getReservedRooms().size() +
                        " | Check-In Date: " + reservation.getReservedRooms().get(0).getCheckInDate().toString() +
                        " | Check-Out Date: " + reservation.getReservedRooms().get(0).getCheckOutDate().toString() +
                        " | Reservation Amount: $" + reservation.getBookingPrice()
                    );
                    boolean hasReservedRoom = false;
                    for (ReservedRoom reservedRoom : reservation.getReservedRooms()) {
                        if (reservedRoom.getRoom() != null) {
                            System.out.print("Room " + reservedRoom.getRoom().getRoomNumber() + " | ");
                            hasReservedRoom = true;
                        }
                    }
                    if (hasReservedRoom) {
                        System.out.println();
                    }
                }
            } else { // if listOfReservations.size() == 0
                System.out.println("You have no outstanding reservations!");
            }
            System.out.println("");
        } catch (GuestDNEException ex) {
            System.out.println("Error viewing reservation: " + ex.getMessage() + "!\n");
        }
        
        System.out.print("Press ENTER to continue> ");
        sc.nextLine();
    }
    
    private void showValidationErrorsForReservation(Set<ConstraintViolation<Reservation>> violations) {
        System.out.println("\n Input data validation error!");
        
        for (ConstraintViolation violation : violations) {
            System.out.println("\t" + violation.getPropertyPath() + "-" + violation.getInvalidValue() + "; " + violation.getMessage());
        }
        System.out.println("\nPlease try again!");
    }
    
    private void showValidationErrorsForReservedRoom(Set<ConstraintViolation<ReservedRoom>> violations) {
        System.out.println("\n Input data validation error!");
        
        for (ConstraintViolation violation : violations) {
            System.out.println("\t" + violation.getPropertyPath() + "-" + violation.getInvalidValue() + "; " + violation.getMessage());
        }
        System.out.println("\nPlease try again!");
    }
}

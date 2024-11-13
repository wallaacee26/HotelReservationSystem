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
import entity.RoomType;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import util.exception.GuestDNEException;
import util.exception.GuestExistsException;
import util.exception.InvalidLoginCredentialException;
import util.exception.RoomTypeDNEException;

/**
 *
 * @author wallace
 */
public class MainApp {
    private Guest currentGuest;
    private GuestSessionBeanRemote guestSBRemote;
    private ReservationSessionBeanRemote reservationSBRemote;
    private RoomTypeSessionBeanRemote roomTypeSBRemote;
    private RoomRateSessionBeanRemote roomRateSBRemote;
    private ReservedRoomSessionBeanRemote reservedRoomSBRemote;

    public MainApp() {
        currentGuest = null;
    }
    
    public MainApp(GuestSessionBeanRemote guestSBRemote, ReservationSessionBeanRemote reservationSBRemote,
            RoomTypeSessionBeanRemote roomTypeSBRemote, RoomRateSessionBeanRemote roomRateSBRemote,
            ReservedRoomSessionBeanRemote reservedRoomSBRemote) {
        this.guestSBRemote = guestSBRemote;
        this.reservationSBRemote = reservationSBRemote;
        this.roomTypeSBRemote = roomTypeSBRemote;
        this.roomRateSBRemote = roomRateSBRemote;
        this.reservedRoomSBRemote = reservedRoomSBRemote;
    }
    
    public void runApp() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            System.out.println("*** Welcome to HoRS Reservation Client ***\n");
            System.out.println("1: Guest Login");
            System.out.println("2: Register as Guest");
            System.out.println("3: Search Hotel Rooms");
            System.out.println("4: Exit\n");
            response = 0;
            
            while(response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();

                if(response == 1) {
                    try {
                        // guest login
                        doGuestLogin();
                        GuestOperationsModule guestOperationsModule = new GuestOperationsModule(guestSBRemote, reservationSBRemote, roomTypeSBRemote,
                                roomRateSBRemote, reservedRoomSBRemote, currentGuest);
                        guestOperationsModule.guestMenu();
                        
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    } catch (GuestDNEException ex) {
                        System.out.println("Error logging in as guest: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    // register as guest
                    doRegisterAsGuest();
                    
                } else if (response == 3) {
                    // search hotel rooms
                    doSearchHotelRoomAsVisitor();
                    
                } else if (response == 4) { // exit
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            if (response == 4) { // exit
                break;
            }
        }
    }

    private void doGuestLogin() throws InvalidLoginCredentialException, GuestDNEException {
        Scanner sc = new Scanner(System.in);
        String email = "";
        String password = "";
        
        System.out.println("*** HoRS Reservation Client :: Guest Login ***\n");
        System.out.print("Enter email> ");
        email = sc.nextLine().trim();
        System.out.print("Enter password> ");
        password = sc.nextLine().trim();
        System.out.println();
        
        try {
            if (email.length() > 0 && password.length() > 0) {
                currentGuest = guestSBRemote.guestLogin(email, password);
            } else {
                throw new InvalidLoginCredentialException("Missing login credential!");
            }
        } catch (GuestDNEException ex) {
            throw new GuestDNEException(ex.getMessage());
        }
    }
    
    private void doRegisterAsGuest() {
        Scanner sc = new Scanner(System.in);
        String email = "";
        String password = "";
        
        System.out.println("*** HoRS Reservation Client :: Register as Guest ***\n");
        System.out.print("Enter email> ");
        email = sc.nextLine().trim();
        System.out.print("Enter password> ");
        password = sc.nextLine().trim();
        
        if (email.length() <= 0 && password.length() <= 0) {
            System.out.println("Error registering as guest! Please enter a valid email and/or password.");
            return;
        }
        
        Guest newGuest = new Guest();
        newGuest.setEmail(email);
        newGuest.setPassword(password);
        
        try {
            Long newGuestId = guestSBRemote.createNewGuest(newGuest);
            System.out.println("New Guest Registered: " + newGuestId + ", with email: " + email);
        } catch (GuestExistsException ex) {
            System.out.println("A guest with this email is already registered! Please try again.\n");
        }
    }
    
    private void doSearchHotelRoomAsVisitor() { // cannot reserve
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** HoRS Reservation Client :: Search Hotel Rooms ***\n");
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
                    System.out.println(i + ": " + roomTypeName + " | Number Of Available Rooms: " + numberOfAvailableRooms + " | Reservation Amount: $" + roomRateSBRemote.calculateTotalRoomRate(roomTypeName, checkInDate, checkOutDate));
                }
            }
            System.out.println();
            
        } catch (DateTimeException ex) {
            System.out.println("Invalid date input(s)! Please try again.");
        } catch (RoomTypeDNEException ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        }
    }
}

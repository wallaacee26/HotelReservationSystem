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
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
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

    public GuestOperationsModule() {
    }
    

    public GuestOperationsModule(GuestSessionBeanRemote guestSBRemote, ReservationSessionBeanRemote reservationSBRemote,
            RoomTypeSessionBeanRemote roomTypeSBRemote, RoomRateSessionBeanRemote roomRateSBRemote,
            ReservedRoomSessionBeanRemote reservedRoomSBRemote, Guest currentGuest) {
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
            System.out.println("1: Search Hotel Rooms"); // not sure for 1 and 2 since 2 includes 1
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
                    System.out.println("Enter your Reservation ID> ");
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
        System.out.print("Enter Check-In Date (Format: DD/MM/YYYY)> ");
        String[] checkInInput = sc.nextLine().split("/");
        System.out.print("Enter Check-Out Date (Format: DD/MM/YYYY)> ");
        String[] checkOutInput = sc.nextLine().split("/");
        
        if (checkInInput.length != 3 || checkOutInput.length != 3) {
            System.out.println("Invalid date input(s)! Please try again.");
            return;
        }
        
        try {
            LocalDate checkInDate = LocalDate.of(Integer.parseInt(checkInInput[2]), Integer.parseInt(checkInInput[1]), Integer.parseInt(checkInInput[0]));
            LocalDate checkOutDate = LocalDate.of(Integer.parseInt(checkOutInput[2]), Integer.parseInt(checkOutInput[1]), Integer.parseInt(checkOutInput[0]));
            
            List<RoomType> listOfRoomTypes = roomTypeSBRemote.searchAvailableRoomTypes(checkInDate, checkOutDate);
            
            if (listOfRoomTypes.size() == 0) {
                System.out.println("There are no available rooms!");
            } else {
                System.out.println("Available Room Types:");
                for (int i = 1; i <= listOfRoomTypes.size(); i++) {
                    String roomTypeName = listOfRoomTypes.get(i - 1).getRoomTypeName();
                    int numberOfAvailableRooms = roomTypeSBRemote.findNumberOfAvailableRoomsForRoomType(roomTypeName, checkInDate, checkOutDate);
                    System.out.println(i + ": " + roomTypeName + " | Number Of Available Rooms: " + numberOfAvailableRooms + " | Reservation Amount: $" + roomRateSBRemote.calculateTotalRoomRate(roomTypeName, checkInDate, checkOutDate));
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
        }
    }
    
    private void doReserveHotelRoom(LocalDate checkInDate, LocalDate checkOutDate) throws RoomTypeDNEException, ReservationExistsException {
        try {
            Scanner sc = new Scanner(System.in);
            Reservation reservation = new Reservation();
            
            // guest - reservation association
            currentGuest.getReservations().add(reservation);
            reservation.setGuest(currentGuest);
            reservationSBRemote.createNewReservation(reservation); // create new reservation first
            
            String response = "Y";
            while (response.equals("Y")) {
                System.out.println("*** HoRS Reservation Client :: Reserve Hotel Room ***\n");
                System.out.print("Enter Room Type to reserve (e.g. Deluxe Room)> ");
                String roomTypeName = sc.nextLine().trim();
                RoomType roomType = roomTypeSBRemote.retrieveRoomTypeByRoomTypeName(roomTypeName);

                ReservedRoom reservedRoom = new ReservedRoom();
                reservedRoom.setCheckInDate(checkInDate);
                reservedRoom.setCheckOutDate(checkOutDate);
                reservedRoom.setIsUpgraded(false); // initially not upgraded

                // associations
                reservedRoom.setReservation(reservation);
                reservation.getReservedRooms().add(reservedRoom);
                
                reservedRoom.setRoomType(roomType);
                roomType.getReservedRooms().add(reservedRoom);
                
                reservedRoomSBRemote.createNewReservedRoom(reservedRoom);
                
                System.out.println("A " + roomTypeName + " has successfully been reserved! Would you like to reserve more hotel rooms? (Y/N)> ");
                response = sc.nextLine().trim();
                
                if (!response.equals("Y")) {
                    break;
                }
            }
 
        } catch (RoomTypeDNEException ex) {
            throw new RoomTypeDNEException(ex.getMessage());
        } catch (ReservationExistsException ex) {
            throw new ReservationExistsException(ex.getMessage());
        }
        
    }
    
    
    
    
    private void doViewAReservation(Long reservationId) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS Reservation Client :: View My Reservation Details ***\n");
        
        try {
            Reservation reservation = reservationSBRemote.retrieveReservationByReservationId(reservationId);
//            System.out.println(
//                    "Reservation ID: " + reservation.getReservationId() +
//                    " | Description: " + reservation.getDescription() +
//                    " | Room Size: " + reservation.getRoomSize() +
//                    " | Capacity: " + rt.getCapacity() +
//                    " | Beds: " + rt.getBeds() + 
//                    " | Amenities: " + rt.getAmenities() + 
//                    " | isDisabled: " + rt.isDisabled()
//            ); // to check what to print for reservations
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
        //            System.out.println(
        //                    "Reservation ID: " + reservation.getReservationId() +
        //                    " | Description: " + reservation.getDescription() +
        //                    " | Room Size: " + reservation.getRoomSize() +
        //                    " | Capacity: " + rt.getCapacity() +
        //                    " | Beds: " + rt.getBeds() + 
        //                    " | Amenities: " + rt.getAmenities() + 
        //                    " | isDisabled: " + rt.isDisabled()
        //            ); // to check what to print for reservations
                }
            } else { // if listOfReservations.size() == 0
                System.out.println("You have no outstanding reservations!");
            }
        } catch (GuestDNEException ex) {
            System.out.println("Error viewing reservation: " + ex.getMessage() + "!\n");
        }
        
        System.out.print("Press ENTER to continue> ");
        sc.nextLine();
    }
}

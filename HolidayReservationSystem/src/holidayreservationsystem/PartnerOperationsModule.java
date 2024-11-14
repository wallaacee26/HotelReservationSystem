/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package holidayreservationsystem;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.partner.Partner;
import ws.partner.PartnerDNEException;
import ws.partner.PartnerDNEException_Exception;
import ws.partner.PartnerWebService_Service;
import ws.reservation.GuestDNEException;
import ws.reservation.GuestDNEException_Exception;
import ws.reservation.Reservation;
import ws.reservation.ReservationDNEException;
import ws.reservation.ReservationDNEException_Exception;
import ws.reservation.ReservationExistsException;
import ws.reservation.ReservationExistsException_Exception;
import ws.reservation.ReservationWebService_Service;
import ws.reservation.ReservedRoom;
import ws.reservedroom.ReservedRoomWebService_Service;
import ws.roomrate.RoomRateWebService_Service;
import ws.roomtype.RoomType;
import ws.roomtype.RoomTypeDNEException;
import ws.roomtype.RoomTypeDNEException_Exception;
import ws.roomtype.RoomTypeWebService_Service;

/**
 *
 * @author wallace
 */
public class PartnerOperationsModule {
    private Partner currentPartner;
    private PartnerWebService_Service partnerService;
    private ReservationWebService_Service reservationService;
    private RoomTypeWebService_Service roomTypeService;
    private RoomRateWebService_Service roomRateService;
    private ReservedRoomWebService_Service reservedRoomService;

    public PartnerOperationsModule() {
    }
    
    public PartnerOperationsModule(PartnerWebService_Service partnerService, ReservationWebService_Service reservationService,
            RoomTypeWebService_Service roomTypeService, RoomRateWebService_Service roomRateService,
            ReservedRoomWebService_Service reservedRoomService, Partner currentPartner) {
        this.partnerService = partnerService;
        this.reservationService = reservationService;
        this.roomTypeService = roomTypeService;
        this.roomRateService = roomRateService;
        this.reservedRoomService = reservedRoomService;
        this.currentPartner = currentPartner;
    }
    
    public void partnerMenu() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            System.out.println("*** Holiday Reservation System ***\n");
            System.out.println("Welcome! You are logged in as " + currentPartner.getUsername() + "!");
            System.out.println("1: Search Hotel Rooms"); // not sure for 1 and 2 since 2 includes 1
            System.out.println("2: View Partner Reservation Details");
            System.out.println("3: View All Partner Reservations");
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
        
        System.out.println("*** Holiday Reservation System :: Search Hotel Rooms ***\n");
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
            
            // get JAX-WS approved date
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            XMLGregorianCalendar checkInDateXML = datatypeFactory.newXMLGregorianCalendarDate(Integer.parseInt(checkInInput[2]),
                Integer.parseInt(checkInInput[1]), Integer.parseInt(checkInInput[0]), 0);
            XMLGregorianCalendar checkOutDateXML = datatypeFactory.newXMLGregorianCalendarDate(Integer.parseInt(checkOutInput[2]),
                Integer.parseInt(checkOutInput[1]), Integer.parseInt(checkOutInput[0]), 0);
            
                
            List<Integer> listOfAllRoomTypes = roomTypeService.getRoomTypeWebServicePort().searchAvailableRoomTypesWithNumberOfRooms(checkInDateXML, checkOutDateXML);
            
            List<RoomType> availableRoomTypes = new ArrayList<>();
            List<Integer> availableRoomsPerRoomType = new ArrayList<>();
            
            for (int i = 0; i < listOfAllRoomTypes.size(); i++) {
                if (listOfAllRoomTypes.get(i) > 0) { // i == 0 will never be > 0, so never triggered
                    availableRoomTypes.add(roomTypeService.getRoomTypeWebServicePort().retrieveRoomTypeByRoomTypeId((long) i));
                    availableRoomsPerRoomType.add(listOfAllRoomTypes.get(i));
                }
            }
            
            if (availableRoomTypes.size() == 0) {
                System.out.println("There are no available rooms!");
                System.out.println();
            } else {
                System.out.println("Available Room Types:");
                for (int i = 1; i <= availableRoomTypes.size(); i++) {
                    String roomTypeName = availableRoomTypes.get(i - 1).getRoomTypeName();
                    int numberOfAvailableRooms = availableRoomsPerRoomType.get(i - 1);
                    // int numberOfAvailableRooms = roomTypeSBRemote.findNumberOfAvailableRoomsForRoomType(roomTypeName, checkInDate, checkOutDate);
                    System.out.println(i + ": " + roomTypeName + " | Number Of Available Rooms: " + numberOfAvailableRooms + " | Reservation Amount: $" + roomRateService.getRoomRateWebServicePort().calculateTotalRoomRateWithNormalRate(roomTypeName, checkInDateXML, checkOutDateXML));
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
                    doReserveHotelRoom(checkInDateXML, checkOutDateXML);
                    break; // dont repeat same UI
                    
                } else if (response == 2) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
        } catch (DateTimeException ex) {
            System.out.println("Invalid date input(s)! Please try again.");
        } catch (ws.roomtype.RoomTypeDNEException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (DatatypeConfigurationException ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ws.roomrate.RoomTypeDNEException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ReservationExistsException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ws.reservation.ReservationDNEException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ws.reservedroom.RoomTypeDNEException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ws.reservedroom.ReservationDNEException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ws.partner.PartnerDNEException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } 
    }
    
    private void doReserveHotelRoom(XMLGregorianCalendar checkInDate, XMLGregorianCalendar checkOutDate)
            throws RoomTypeDNEException_Exception, ReservationExistsException_Exception, ws.reservation.ReservationDNEException_Exception,
            ws.reservedroom.ReservationDNEException_Exception, ws.reservedroom.RoomTypeDNEException_Exception, PartnerDNEException_Exception {
        try {
            Scanner sc = new Scanner(System.in);
            Reservation reservation = new Reservation();
            Long reservationId = (long) -1; // simply for initialisation
            
            // guest - reservation association
            ws.reservation.Partner webPartner = new ws.reservation.Partner();
            webPartner.setPartnerId(currentPartner.getPartnerId());
            webPartner.setUsername(currentPartner.getUsername());
            webPartner.setPassword(currentPartner.getPassword());
            
            reservation.setPartner(webPartner);
            boolean hasReservationBeenCreated = false;
                
            String response = "Y";
            while (response.equals("Y")) {
                System.out.println("*** Holiday Reservation Client :: Reserve Hotel Room ***\n");
                System.out.print("Enter Room Type to reserve (e.g. Deluxe Room)> ");
                String roomTypeName = "";
                roomTypeName = sc.nextLine().trim();
                System.out.println("");
                
                boolean inputFlag = false;
                while (inputFlag == false) {
                    while (roomTypeName.length() == 0) {
                        System.out.print("No input detected. Please enter a room type> ");
                        roomTypeName = sc.nextLine().trim();
                    }

                    List<RoomType> allRoomTypes = roomTypeService.getRoomTypeWebServicePort().retrieveAllRoomTypes();
                    boolean isValid = false;
                    while (!isValid) {
                        for (RoomType roomType : allRoomTypes) {
                            if (roomTypeName.equals(roomType.getRoomTypeName())) {
                                isValid = true;
                                break;
                            }
                        }
                        if (!isValid) {
                            // if input room type is not valid, then prompt for re-input
                            System.out.println("Error in getting room type, please enter a valid room type.\n");
                            // roomTypeName = sc.nextLine().trim();
                            return;
                        }
                    }

                    // if available, return true (move on), else if not available, this method returns false and returns
                    if (!roomTypeService.getRoomTypeWebServicePort().checkAvailabilityForRoomType(roomTypeName, checkInDate, checkOutDate)) {
                        System.out.print("There are no available rooms for this Room Type! Please try again> ");
                        roomTypeName = sc.nextLine().trim();
                    } else {
                        break;
                    }
                }
                
                RoomType roomType = roomTypeService.getRoomTypeWebServicePort().retrieveRoomTypeByRoomTypeName(roomTypeName);
                
                // creates the reservation if not already created before (to link reserved rooms to the same reservation object)
                // will only reach this stage when roomType is valid, so it is safe to link reservationId here
                if (!hasReservationBeenCreated) {
                    reservationId = reservationService.getReservationWebServicePort().createNewReservation(reservation); // create new reservation first
                    reservationService.getReservationWebServicePort().associateReservationWithPartner(reservationId, currentPartner.getPartnerId());
                    hasReservationBeenCreated = true;
                    System.out.println("Your Reservation ID is : " + reservationId + "!");
                }
            
                ws.reservedroom.ReservedRoom reservedRoom = new ws.reservedroom.ReservedRoom();
                reservedRoom = reservedRoomService.getReservedRoomWebServicePort().associateReservedRoomWithDatesWebService(reservedRoom, checkInDate, checkOutDate);
                reservedRoom.setIsUpgraded(false); // initially not upgraded
                
                reservedRoomService.getReservedRoomWebServicePort().createNewReservedRoom(reservedRoom, reservationId, roomType.getRoomTypeId());
                
                System.out.print("A " + roomTypeName + " has successfully been reserved! Would you like to reserve more hotel rooms? (Y/N)> ");
                response = sc.nextLine().trim();
                System.out.println("");
                
                if (!response.equals("Y")) {
                    break;
                }
            }
 
        } catch (RoomTypeDNEException_Exception ex) {
            throw new RoomTypeDNEException_Exception(ex.getMessage(), new RoomTypeDNEException());
        } catch (ReservationExistsException_Exception ex) {
            throw new ReservationExistsException_Exception(ex.getMessage(), new ReservationExistsException());
        } catch (ws.reservation.ReservationDNEException_Exception ex) {
            throw new ReservationDNEException_Exception(ex.getMessage(), new ReservationDNEException());
        } catch (ws.reservedroom.ReservationDNEException_Exception ex) {
            throw new ReservationDNEException_Exception(ex.getMessage(), new ReservationDNEException());
        } catch (ws.reservation.PartnerDNEException_Exception ex) {
            throw new PartnerDNEException_Exception(ex.getMessage(), new PartnerDNEException());
        }
    }
    
    
    
    
    private void doViewAReservation(Long reservationId) {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Holiday Reservation System :: View Partner Reservation Details ***\n");
        
        try {
            Reservation reservation = reservationService.getReservationWebServicePort().retrieveReservationByReservationId(reservationId);
            System.out.println(
                "Reservation ID: " + reservation.getReservationId() +
                " | Number Of Reserved Rooms: " + reservation.getReservedRooms().size() +
                " | Check-In Date: " + reservation.getReservedRooms().get(0).getCheckInDate().toString() +
                " | Check-Out Date: " + reservation.getReservedRooms().get(0).getCheckOutDate().toString()
            );
            for (ReservedRoom reservedRoom : reservation.getReservedRooms()) {
                if (reservedRoom.getRoom() != null) {
                    System.out.print("Room " + reservedRoom.getRoom().getRoomNumber() + " | ");
                }
            }
            System.out.println("");
        } catch (ReservationDNEException_Exception ex) {
            System.out.println("Error viewing reservation: " + ex.getMessage() + "!\n");
        }
        
        System.out.print("Press ENTER to continue> ");
        sc.nextLine();
    }
    
    private void doViewAllMyReservations() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** Holiday Reservation System :: View All Partner Reservations ***\n");
        
        try {
            List<Reservation> listOfReservations = reservationService.getReservationWebServicePort().retrieveAllReservationsOfPartnerId(currentPartner.getPartnerId());
            if (listOfReservations.size() > 0) {
                for (Reservation reservation : listOfReservations) {
                    System.out.println(
                            "Reservation ID: " + reservation.getReservationId() +
                            " | Number Of Reserved Rooms: " + reservation.getReservedRooms().size() +
                            " | Check-In Date: " + reservation.getReservedRooms().get(0).getCheckInDate().toString() +
                            " | Check-Out Date: " + reservation.getReservedRooms().get(0).getCheckOutDate().toString()
                    );
                    for (ReservedRoom reservedRoom : reservation.getReservedRooms()) {
                        if (reservedRoom.getRoom() != null) {
                            System.out.print("Room " + reservedRoom.getRoom().getRoomNumber() + " | ");
                        }
                    }
                }
            } else { // if listOfReservations.size() == 0
                System.out.println("You have no outstanding reservations!");
            }
            System.out.println("");
        } catch (ws.reservation.PartnerDNEException_Exception ex) {
            System.out.println("Error viewing reservation: " + ex.getMessage() + "!\n");
        }
        
        System.out.print("Press ENTER to continue> ");
        sc.nextLine();
    }
}

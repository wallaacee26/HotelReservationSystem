/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package holidayreservationsystem;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.partner.Partner;
import ws.partner.PartnerWebService_Service;
import ws.reservation.Reservation;
import ws.reservation.ReservationExistsException_Exception;
import ws.reservation.ReservationWebService_Service;
import ws.reservation.ReservedRoom;
import ws.reservedroom.ReservedRoomWebService_Service;
import ws.roomrate.RoomRateWebService_Service;
import ws.roomtype.RoomType;
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
            System.out.println("1: Search Hotel Rooms");
            System.out.println("2: View Partner Reservation Details");
            System.out.println("3: View All Partner Reservations");
            System.out.println("4: Logout");
            response = 0;
            
            while(response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if (response == 1) {
                    // search hotel rooms
                    doSearchHotelRoomAsPartner();
                    
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
    
    private void doSearchHotelRoomAsPartner() { // can reserve
        Scanner sc = new Scanner(System.in);
        
        System.out.println("*** Holiday Reservation System :: Search Hotel Rooms ***\n");
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
            
            // get JAX-WS approved date
            DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
            
            GregorianCalendar checkInGC = GregorianCalendar.from(checkInDate.atStartOfDay(ZoneId.systemDefault()));
            XMLGregorianCalendar checkInDateXML = datatypeFactory.newXMLGregorianCalendar(checkInGC);
            GregorianCalendar checkOutGC = GregorianCalendar.from(checkOutDate.atStartOfDay(ZoneId.systemDefault()));
            XMLGregorianCalendar checkOutDateXML = datatypeFactory.newXMLGregorianCalendar(checkOutGC);
                
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
                    System.out.println(i + ": " + roomTypeName + " | Number Of Available Rooms: " + numberOfAvailableRooms + " | Reservation Amount (for duration): $" + roomRateService.getRoomRateWebServicePort().calculateTotalRoomRateWithNormalRate(roomTypeName, checkInDateXML, checkOutDateXML));
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
        } catch (DatatypeConfigurationException ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ws.roomtype.RoomTypeDNEException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ws.roomrate.RoomTypeDNEException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ws.reservation.ReservationExistsException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ws.reservation.ReservationDNEException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ws.reservedroom.RoomTypeDNEException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ws.reservedroom.ReservationDNEException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ws.reservation.PartnerDNEException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        }
    }
    
    private void doReserveHotelRoom(XMLGregorianCalendar checkInDate, XMLGregorianCalendar checkOutDate)
            throws RoomTypeDNEException_Exception, ReservationExistsException_Exception, ws.reservation.ReservationDNEException_Exception,
            ws.reservedroom.ReservationDNEException_Exception, ws.reservedroom.RoomTypeDNEException_Exception, ws.reservation.PartnerDNEException_Exception,
            ws.roomrate.RoomTypeDNEException_Exception {
        try {
            Scanner sc = new Scanner(System.in);
            Reservation reservation = new Reservation();
            Long reservationId = (long) -1; // simply for initialisation
            BigDecimal totalBookingAmount = BigDecimal.ZERO; // simply for initialisation
            reservation.setBookingPrice(totalBookingAmount); // simply for initialisation
            
            // partner - reservation association
            // to overcome error in web service
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
                        System.out.println();
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
                }
            
                ws.reservedroom.ReservedRoom reservedRoom = new ws.reservedroom.ReservedRoom();
                reservedRoom.setIsUpgraded(false); // initially not upgraded
                
                reservedRoomService.getReservedRoomWebServicePort().createNewReservedRoomWebService(reservedRoom, 
                    reservationId, roomType.getRoomTypeId(), checkInDate, checkOutDate);
                
                LocalDate today = LocalDate.now();
                //convert XMLGregorianCalender to localdate
                LocalDate checkInLocalDate = checkInDate.toGregorianCalendar().toZonedDateTime().toLocalDate();
                if (checkInLocalDate.isEqual(today)) { // force allocation if the room is reserved for today's checkin
                    reservedRoomService.getReservedRoomWebServicePort().allocateRooms();
                }
                
                totalBookingAmount = totalBookingAmount.add(roomRateService.getRoomRateWebServicePort().calculateTotalRoomRateWithNormalRate(roomTypeName, checkInDate, checkOutDate));
                reservationService.getReservationWebServicePort().updateReservationBookingAmount(reservationId, totalBookingAmount);
                
                System.out.print("A " + roomTypeName + " has successfully been reserved! Would you like to reserve more hotel rooms? (Y/N)> ");
                response = sc.nextLine().trim();
                System.out.println("");
                
                if (!response.equals("Y")) {
                    System.out.println("Successful reservation made! Your Reservation ID is: " + reservationId + "!");
                    break;
                }
            }
 
        } catch (ws.reservedroom.RoomTypeDNEException_Exception ex) {
            throw new ws.reservedroom.RoomTypeDNEException_Exception(ex.getMessage(), new ws.reservedroom.RoomTypeDNEException());
            
        } catch (ws.reservation.ReservationExistsException_Exception ex) {
            throw new ws.reservation.ReservationExistsException_Exception(ex.getMessage(), new ws.reservation.ReservationExistsException());
            
        } catch (ws.reservation.ReservationDNEException_Exception ex) {
            throw new ws.reservation.ReservationDNEException_Exception(ex.getMessage(), new ws.reservation.ReservationDNEException());
            
        } catch (ws.reservedroom.ReservationDNEException_Exception ex) {
            throw new ws.reservedroom.ReservationDNEException_Exception(ex.getMessage(), new ws.reservedroom.ReservationDNEException());
            
        } catch (ws.reservation.PartnerDNEException_Exception ex) {
            throw new ws.reservation.PartnerDNEException_Exception(ex.getMessage(), new ws.reservation.PartnerDNEException());
            
        } catch (ws.roomrate.RoomTypeDNEException_Exception ex) {
            throw new ws.roomrate.RoomTypeDNEException_Exception(ex.getMessage(), new ws.roomrate.RoomTypeDNEException());
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
                " | Check-In Date: " + reservationService.getReservationWebServicePort().getStringOfCheckInDate(reservation.getReservationId()) +
                " | Check-Out Date: " + reservationService.getReservationWebServicePort().getStringOfCheckOutDate(reservation.getReservationId())+
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
        } catch (ws.reservation.ReservationDNEException_Exception ex) {
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
                        " | Check-In Date: " + reservationService.getReservationWebServicePort().getStringOfCheckInDate(reservation.getReservationId()) +
                        " | Check-Out Date: " + reservationService.getReservationWebServicePort().getStringOfCheckOutDate(reservation.getReservationId())+
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
        } catch (ws.reservation.PartnerDNEException_Exception ex) {
            System.out.println("Error viewing reservation: " + ex.getMessage() + "!\n");
        } catch (ws.reservation.ReservationDNEException_Exception ex) {
            System.out.println("Error viewing reservation: " + ex.getMessage() + "!\n");
        }
        
        System.out.print("Press ENTER to continue> ");
        sc.nextLine();
    }
}

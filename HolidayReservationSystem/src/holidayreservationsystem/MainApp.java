/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package holidayreservationsystem;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.partner.InvalidLoginCredentialException;
import ws.partner.InvalidLoginCredentialException_Exception;
import ws.partner.Partner;
import ws.partner.PartnerDNEException;
import ws.partner.PartnerDNEException_Exception;
import ws.partner.PartnerWebService_Service;
import ws.reservation.ReservationWebService_Service;
import ws.reservedroom.ReservedRoomWebService_Service;
import ws.roomrate.RoomRateWebService_Service;
import ws.roomtype.RoomType;
import ws.roomtype.RoomTypeWebService_Service;

/**
 *
 * @author wallace
 */

public class MainApp {
    private Partner currentPartner;
    private PartnerWebService_Service partnerService;
    private ReservationWebService_Service reservationService;
    private RoomTypeWebService_Service roomTypeService;
    private RoomRateWebService_Service roomRateService;
    private ReservedRoomWebService_Service reservedRoomService;

    public MainApp() {
        currentPartner = null;
    }
    
    public MainApp(PartnerWebService_Service partnerService, ReservationWebService_Service reservationService,
            RoomTypeWebService_Service roomTypeService, RoomRateWebService_Service roomRateService,
            ReservedRoomWebService_Service reservedRoomService) {
        this.partnerService = partnerService;
        this.reservationService = reservationService;
        this.roomTypeService = roomTypeService;
        this.roomRateService = roomRateService;
        this.reservedRoomService = reservedRoomService;
    }
    
    public void runApp() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            System.out.println("*** Welcome to Holiday Reservation System ***\n");
            System.out.println("1: Partner Login");
            System.out.println("2: Search Hotel Rooms");
            System.out.println("3: Exit\n");
            response = 0;
            
            while(response < 1 || response > 3) {
                System.out.print("> ");
                response = sc.nextInt();

                if(response == 1) {
                    try {
                        // partner login
                        doPartnerLogin();
                        PartnerOperationsModule partnerOperationsModule = new PartnerOperationsModule(partnerService, reservationService, roomTypeService,
                                roomRateService, reservedRoomService, currentPartner);
                        partnerOperationsModule.partnerMenu();
                        
                    } catch (InvalidLoginCredentialException_Exception ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    } catch (PartnerDNEException_Exception ex) {
                        System.out.println("Error logging in as partner: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    // search hotel rooms
                    doSearchHotelRoomAsPartnerEmployee();
                    
                } else if (response == 3) { // exit
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            if (response == 3) { // exit
                break;
            }
        }
    }

    private void doPartnerLogin() throws InvalidLoginCredentialException_Exception, PartnerDNEException_Exception {
        Scanner sc = new Scanner(System.in);
        String email = "";
        String password = "";
        
        System.out.println("*** HoRS Reservation Client :: Partner Login ***\n");
        System.out.print("Enter email> ");
        email = sc.nextLine().trim();
        System.out.print("Enter password> ");
        password = sc.nextLine().trim();
        System.out.println();
        
        try {
            if (email.length() > 0 && password.length() > 0) {
                currentPartner = partnerService.getPartnerWebServicePort().partnerLogin(email, password);
            } else {
                throw new InvalidLoginCredentialException_Exception("Missing login credential!", new InvalidLoginCredentialException());
            }
        } catch (PartnerDNEException_Exception ex) {
            throw new PartnerDNEException_Exception(ex.getMessage(), new PartnerDNEException());
        }
    }
    
    private void doSearchHotelRoomAsPartnerEmployee() { // cannot reserve
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
            } else {
                System.out.println("Available Room Types:");
                for (int i = 1; i <= availableRoomTypes.size(); i++) {
                    String roomTypeName = availableRoomTypes.get(i - 1).getRoomTypeName();
                    int numberOfAvailableRooms = availableRoomsPerRoomType.get(i - 1);
                    // int numberOfAvailableRooms = roomTypeSBRemote.findNumberOfAvailableRoomsForRoomType(roomTypeName, checkInDate, checkOutDate);
                    System.out.println(i + ": " + roomTypeName + " | Number Of Available Rooms: " + numberOfAvailableRooms + " | Reservation Amount: $" + roomRateService.getRoomRateWebServicePort().calculateTotalRoomRateWithNormalRate(roomTypeName, checkInDateXML, checkOutDateXML));
                }
            }
            System.out.println();
            
        } catch (DateTimeException ex) {
            System.out.println("Invalid date input(s)! Please try again.");
        } catch (ws.roomtype.RoomTypeDNEException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (DatatypeConfigurationException ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        } catch (ws.roomrate.RoomTypeDNEException_Exception ex) {
            System.out.println("Error while retrieving rooms: " + ex.getMessage() + " Please try again.");
        }
    }
}
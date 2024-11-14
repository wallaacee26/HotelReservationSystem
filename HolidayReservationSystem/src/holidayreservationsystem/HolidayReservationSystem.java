/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package holidayreservationsystem;

import ws.partner.PartnerWebService_Service;
import ws.reservation.ReservationWebService_Service;
import ws.reservedroom.ReservedRoomWebService_Service;
import ws.roomrate.RoomRateWebService_Service;
import ws.roomtype.RoomTypeWebService_Service;

/**
 *
 * @author wallace
 */
public class HolidayReservationSystem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PartnerWebService_Service partnerService = new PartnerWebService_Service();
        ReservationWebService_Service reservationService = new ReservationWebService_Service();
        RoomTypeWebService_Service roomTypeService = new RoomTypeWebService_Service();
        RoomRateWebService_Service roomRateService = new RoomRateWebService_Service();
        ReservedRoomWebService_Service reservedRoomService = new ReservedRoomWebService_Service();
        
        MainApp mainApp = new MainApp(partnerService, reservationService, roomTypeService, roomRateService, reservedRoomService);
        mainApp.runApp();
    }
    
}

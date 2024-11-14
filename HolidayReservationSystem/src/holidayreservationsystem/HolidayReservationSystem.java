/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package holidayreservationsystem;

import ws.reservation.ReservationWebService_Service;
import ws.reservedroom.ReservedRoomWebService_Service;

/**
 *
 * @author wallace
 */
public class HolidayReservationSystem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ReservationWebService_Service reservationService = new ReservationWebService_Service();
        ReservedRoomWebService_Service reservedRoomService = new ReservedRoomWebService_Service();
        
        // must get port to invoke methods
        // reservationService.getReservationWebServicePort()...
        // reservedRoomService.getReservedRoomWebServicePort()...
    }
    
}

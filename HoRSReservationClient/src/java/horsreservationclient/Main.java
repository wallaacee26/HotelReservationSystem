package horsreservationclient;

import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.ReservedRoomSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author wallace
 */
public class Main {
    
    @EJB
    private static GuestSessionBeanRemote guestSBRemote;
    @EJB
    private static ReservationSessionBeanRemote reservationSBRemote;
    @EJB
    private static RoomTypeSessionBeanRemote roomTypeSBRemote;
    @EJB
    private static RoomRateSessionBeanRemote roomRateSBRemote;
    @EJB
    private static ReservedRoomSessionBeanRemote reservedRoomSBRemote;
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(guestSBRemote, reservationSBRemote, roomTypeSBRemote, roomRateSBRemote, reservedRoomSBRemote);
        mainApp.runApp();
    }
    
}

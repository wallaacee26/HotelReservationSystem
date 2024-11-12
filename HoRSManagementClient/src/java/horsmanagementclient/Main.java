package horsmanagementclient;

import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservedRoomSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import ejb.session.stateless.StaffSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author wallace
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    
    @EJB
    private static StaffSessionBeanRemote staffSBRemote;
    @EJB
    private static PartnerSessionBeanRemote partnerSBRemote;
    @EJB
    private static RoomTypeSessionBeanRemote roomTypeSBRemote;
    @EJB
    private static RoomSessionBeanRemote roomSBRemote;
    @EJB
    private static RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    @EJB
    private static ReservedRoomSessionBeanRemote reservedRoomSessionBeanRemote;
    
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(staffSBRemote, partnerSBRemote, 
                roomTypeSBRemote, roomSBRemote, roomRateSessionBeanRemote, reservedRoomSessionBeanRemote);
        mainApp.runApp();
    }
    
}

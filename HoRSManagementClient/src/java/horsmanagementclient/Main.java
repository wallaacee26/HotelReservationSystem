package horsmanagementclient;

import ejb.session.stateless.PartnerSessionBeanRemote;
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
    
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(staffSBRemote, partnerSBRemote);
        mainApp.runApp();
    }
    
}

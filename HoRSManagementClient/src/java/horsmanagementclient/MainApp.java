/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import ejb.session.stateless.StaffSessionBeanRemote;
import entity.Staff;
import java.util.Scanner;
import util.enumeration.AccessRightEnum;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author yewkhang
 */
public class MainApp {
    private Staff currentStaff;
    private StaffSessionBeanRemote staffSBRemote;
    private PartnerSessionBeanRemote partnerSBRemote;
    private RoomTypeSessionBeanRemote roomTypeSBRemote;
    private RoomSessionBeanRemote roomSBRemote;
    
    private AdministratorModule adminModule;
    private OperationsModule operationsModule;

    public MainApp() {
        currentStaff = null;
    }
    
    public MainApp(StaffSessionBeanRemote staffSBRemote, PartnerSessionBeanRemote partnerSBRemote,
            RoomTypeSessionBeanRemote roomTypeSBRemote, RoomSessionBeanRemote roomSBRemote) {
        this.staffSBRemote = staffSBRemote;
        this.partnerSBRemote = partnerSBRemote;
        this.roomTypeSBRemote = roomTypeSBRemote;
        this.roomSBRemote = roomSBRemote; 
    }
    
    public void runApp() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            System.out.println("*** Welcome to HoRS Management Client ***\n");
            System.out.println("1: Login");
            System.out.println("2: Exit\n");
            response = 0;
            
            while(response < 1 || response > 2) {
                System.out.print("> ");
                response = sc.nextInt();

                if(response == 1) {
                    try {
                        doStaffLogin();
                        // separate modules by accessRights
                        if (currentStaff.getAccessRights().equals(AccessRightEnum.ADMINISTRATOR)) {
                            // do administrator things
                            adminModule = new AdministratorModule(staffSBRemote, partnerSBRemote, currentStaff);
                            adminModule.adminMenu();
                        } else if (currentStaff.getAccessRights().equals(AccessRightEnum.OPERATIONS)) {
                            // do operation manager things
                            operationsModule = new OperationsModule(roomTypeSBRemote, roomSBRemote, currentStaff);
                            operationsModule.adminMenu();
                        }
                    } catch (InvalidLoginCredentialException ex) {
                        System.out.println("Invalid login credential: " + ex.getMessage() + "\n");
                    }
                } else if (response == 2) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            if (response == 2) { // exit
                break;
            }
        }
    }

    private void doStaffLogin() throws InvalidLoginCredentialException{
        Scanner sc = new Scanner(System.in);
        String username = "";
        String password = "";
        
        System.out.println("*** HoRS Management Client :: Login ***\n");
        System.out.print("Enter username> ");
        username = sc.nextLine().trim();
        System.out.print("Enter password> ");
        password = sc.nextLine().trim();
        
        if(username.length() > 0 && password.length() > 0) {
            currentStaff = staffSBRemote.staffLogin(username, password);
        } else {
            throw new InvalidLoginCredentialException("Missing login credential!");
        }
    }
    
}

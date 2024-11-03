/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.PartnerSessionBeanRemote;
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

    public MainApp() {
        currentStaff = null;
    }
    
    public MainApp(StaffSessionBeanRemote staffSBRemote, PartnerSessionBeanRemote partnerSBRemote) {
        this.staffSBRemote = staffSBRemote;
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
                            adminMenu();
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
    
    private void adminMenu() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            System.out.println("*** HoRS Management Client ***\n");
            System.out.println("You are logged in as " + currentStaff.getUsername() + " with " + currentStaff.getAccessRights().toString() + " rights");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("5: Logout");
            response = 0;
            
            while(response < 1 || response > 5) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if (response == 1) {
                    // create new employee
                } else if (response == 2) {
                    // view all employees
                } else if (response == 3) {
                    // create new partner
                } else if (response == 4) {
                    // view all partners
                } else if (response == 5) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
            if (response == 5) { // logout
                break;
            }
        }
    }
}

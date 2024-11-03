/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.StaffSessionBeanRemote;
import entity.Staff;
import java.util.List;
import java.util.Scanner;
import util.enumeration.AccessRightEnum;
import util.exception.StaffUsernameExistsException;

/**
 *
 * @author yewkhang
 */
public class AdministratorModule {
    private StaffSessionBeanRemote staffSBRemote;
    private Staff currentStaff;

    public AdministratorModule() {
    }
    

    public AdministratorModule(StaffSessionBeanRemote staffSBRemote, Staff currentStaff) {
        this.staffSBRemote = staffSBRemote;
        this.currentStaff = currentStaff;
    }
    
    public void adminMenu() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            System.out.println("*** HoRS Management Client ***\n");
            System.out.println("You are logged in as " + currentStaff.getUsername() + " with " + currentStaff.getAccessRights().toString() + " rights");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("5: Logout\n");
            response = 0;
            
            while(response < 1 || response > 5) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if (response == 1) {
                    // create new employee
                    doCreateNewEmployee();
                } else if (response == 2) {
                    // view all employees
                    doViewAllEmployees();
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
    
    private void doCreateNewEmployee() {
        Scanner sc = new Scanner(System.in);
        Staff newStaff = new Staff();
        
        System.out.println("*** HoRS Management Client :: Create New Employee ***\n");
        System.out.print("Enter Username> ");
        newStaff.setUsername(sc.nextLine().trim());
        System.out.print("Enter Password> ");
        newStaff.setPassword(sc.nextLine().trim());
        
        while(true) {
            System.out.println("Select Access Rights:\n"
                    + "1: System Administrator\n"
                    + "2: Operation Manager\n"
                    + "3: Sales Manager\n"
                    + "4: Guest Relation Officer\n");
            System.out.print("> ");
            Integer accessRightsNum = sc.nextInt();
            
            if (accessRightsNum >= 1 && accessRightsNum <= 4) { //if within enum range
                newStaff.setAccessRights(AccessRightEnum.values()[accessRightsNum - 1]);
                break;
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        try {
            Long newStaffId = staffSBRemote.createNewStaff(newStaff);
            System.out.println("New employee created: " + newStaffId + "\n");
        } catch (StaffUsernameExistsException ex) {
            System.out.println("Error when creating new employee. Username already exists!\n");
        }
    }
    
    private void doViewAllEmployees() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS Management Client :: View All Employees ***\n");
        
        List<Staff> listOfEmployees = staffSBRemote.retrieveAllStaffs();
        for (Staff s : listOfEmployees) {
            System.out.println("ID: " + s.getStaffId() +
                    " | Username: " + s.getUsername() +
                    " | Password: " + s.getPassword() + 
                    " | AccessRights: " + s.getAccessRights().toString());
        }
        
        System.out.print("Press any key to cotinue> ");
        sc.nextLine();
    }
}

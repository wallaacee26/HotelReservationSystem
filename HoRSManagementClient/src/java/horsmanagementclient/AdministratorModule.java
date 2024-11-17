/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.StaffSessionBeanRemote;
import entity.Partner;
import entity.Staff;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.Validator;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import util.enumeration.AccessRightEnum;
import util.exception.PartnerExistsException;
import util.exception.StaffUsernameExistsException;

/**
 *
 * @author yewkhang
 */
public class AdministratorModule {
    private StaffSessionBeanRemote staffSBRemote;
    private PartnerSessionBeanRemote partnerSBRemote;
    private Staff currentStaff;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public AdministratorModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    

    public AdministratorModule(StaffSessionBeanRemote staffSBRemote, PartnerSessionBeanRemote partnerSBRemote, Staff currentStaff) {
        this();
        this.staffSBRemote = staffSBRemote;
        this.partnerSBRemote = partnerSBRemote;
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
                    doCreateNewPartner();
                } else if (response == 4) {
                    // view all partners
                    doViewAllPartners();
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
        
        while(true) { // set access rights
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
        
        Set<ConstraintViolation<Staff>> violations = validator.validate(newStaff);
        if (violations.isEmpty()) {
            try {
                Long newStaffId = staffSBRemote.createNewStaff(newStaff);
                System.out.println("New employee created: " + newStaffId + "\n");
            } catch (StaffUsernameExistsException ex) {
                System.out.println("Error when creating new employee. Username already exists!\n");
            }
        } else {
            showValidationErrorsForStaff(violations);
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
        
        System.out.print("Press ENTER key to continue> ");
        sc.nextLine();
    }
    
    private void doCreateNewPartner() {
        Scanner sc = new Scanner(System.in);
        Partner newPartner = new Partner();
        
        System.out.println("*** HoRS Management Client :: Create New Partner ***\n");
        System.out.print("Enter Username> ");
        newPartner.setUsername(sc.nextLine().trim());
        System.out.print("Enter Password> ");
        newPartner.setPassword(sc.nextLine().trim());
        
        Set<ConstraintViolation<Partner>> violations = validator.validate(newPartner);
        if (violations.isEmpty()) {
            try {
                Long newPartnerId = partnerSBRemote.createNewPartner(newPartner);
                System.out.println("New partner created: " + newPartnerId + "\n");
            } catch (PartnerExistsException ex) {
                System.out.println("Error when creating new partner. Username already exists!\n");
            }
        } else {
            showValidationErrorsForPartner(violations);
        }   
    }
    
    private void doViewAllPartners() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS Management Client :: View All Partners ***\n");
        
        List<Partner> listOfPartners = partnerSBRemote.retrieveAllPartners();
        for (Partner p : listOfPartners) {
            System.out.println("ID: " + p.getPartnerId()+
                    " | Username: " + p.getUsername() +
                    " | Password: " + p.getPassword());
        }
        
        System.out.print("Press ENTER key to continue> ");
        sc.nextLine();
    }
    
    private void showValidationErrorsForStaff(Set<ConstraintViolation<Staff>> violations) {
        System.out.println("\n Input data validation error!");
        
        for (ConstraintViolation violation : violations) {
            System.out.println("\t" + violation.getPropertyPath() + "-" + violation.getInvalidValue() + "; " + violation.getMessage());
        }
        System.out.println("\nPlease try again!");
    }
    
    private void showValidationErrorsForPartner(Set<ConstraintViolation<Partner>> violations) {
        System.out.println("\n Input data validation error!");
        
        for (ConstraintViolation violation : violations) {
            System.out.println("\t" + violation.getPropertyPath() + "-" + violation.getInvalidValue() + "; " + violation.getMessage());
        }
        System.out.println("\nPlease try again!");
    }
}

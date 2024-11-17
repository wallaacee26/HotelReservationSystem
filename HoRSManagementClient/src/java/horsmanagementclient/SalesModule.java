/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package horsmanagementclient;

import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.RoomRate;
import entity.RoomType;
import entity.Staff;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.RateTypeEnum;
import util.exception.RoomRateDNEException;
import util.exception.RoomRateExistsException;
import util.exception.RoomTypeDNEException;
import util.exception.RoomTypeDisabledException;

/**
 *
 * @author yewkhang
 */
public class SalesModule {
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSBRemote;
    private Staff currentStaff;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public SalesModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public SalesModule(RoomRateSessionBeanRemote roomRateSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSBRemote, Staff currentStaff) {
        this();
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomTypeSBRemote = roomTypeSBRemote;
        this.currentStaff = currentStaff;
    }
    
    public void adminMenu() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while(true) {
            System.out.println("*** HoRS Management Client ***\n");
            System.out.println("You are logged in as " + currentStaff.getUsername() + " with " + currentStaff.getAccessRights().toString() + " rights");
            System.out.println("1: Create New Room Rate");
            System.out.println("2: View Room Rate Details");
            System.out.println("3: View All Room Rates");
            System.out.println("4: Logout\n");
            response = 0;
            
            while(response < 1 || response > 4) {
                System.out.print("> ");
                response = sc.nextInt();
                
                if (response == 1) {
                    // create new room rate
                    doCreateNewRoomRate();
                } else if (response == 2) {
                    // view room rate details
                    doViewRoomRateDetails();
                } else if (response == 3) {
                    // view all room rate
                    doViewAllRoomRates();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
            if (response == 4) { // logout
                break;
            }
        }
    }
    
    public void doCreateNewRoomRate() {
        try {
            Scanner sc = new Scanner(System.in);
            RoomRate newRoomRate = new RoomRate();
            String roomTypeName = "";
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            Date startDate;
            Date endDate;

            System.out.println("*** HoRS Management Client :: Create New Room Rate ***\n");
            System.out.print("Enter Room Rate Name> ");
            newRoomRate.setRoomRateName(sc.nextLine().trim());
            System.out.print("Enter Rate Per Night> $");
            newRoomRate.setRatePerNight(BigDecimal.valueOf(sc.nextDouble()));
            sc.nextLine();
            System.out.print("Enter Room Type Name> ");
            roomTypeName = sc.nextLine().trim();
            RoomType rt = roomTypeSBRemote.retrieveRoomTypeByRoomTypeName(roomTypeName);
            newRoomRate.getRoomTypes().add(rt);
            // how to associate??? -> associated in Session bean's create method
            
            while(true) {
                System.out.println("Select Rate Type:\n"
                        + "1: Published Rate\n"
                        + "2: Normal Rate\n"
                        + "3: Peak Rate\n"
                        + "4: Promotion Rate\n");
                System.out.print("> ");
                Integer rateTypeNum = sc.nextInt();

                if (rateTypeNum >= 1 && rateTypeNum <= 4) { //if within enum range
                    newRoomRate.setRateType(RateTypeEnum.values()[rateTypeNum - 1]);
                    sc.nextLine();
                    // if peak or promotion rate
                    if (rateTypeNum == 3 || rateTypeNum == 4) {
                        boolean dateValid = false;
                        while(!dateValid) { // checks if start date is before end date
                            // set start and end date of rates
                            System.out.print("Enter Rate Start Date (dd/mm//yy): ");
                            startDate = inputDateFormat.parse(sc.nextLine().trim());
                            System.out.print("Enter Rate End Date (dd/mm//yy): ");
                            endDate = inputDateFormat.parse(sc.nextLine().trim());
                            if (startDate.after(endDate)) {
                                System.out.println("Rate Start Date cannot be after Rate End Date. Please try again!");
                                dateValid = false;
                            } else {
                                dateValid = true;
                                newRoomRate.setStartDate(startDate);
                                newRoomRate.setEndDate(endDate);
                            }
                        }   
                    }
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            Set<ConstraintViolation<RoomRate>> violations = validator.validate(newRoomRate);
            if (violations.isEmpty()) {
                try {
                    Long roomRateId = roomRateSessionBeanRemote.createNewRoomRate(newRoomRate, rt.getRoomTypeName());
                    System.out.println("New Room Rate Created: " + roomRateId + "\n");
                } catch (RoomRateExistsException ex) {
                    System.out.println("Room rate already exists!");
                } catch (RoomTypeDisabledException ex) {
                    System.out.println(ex.getMessage());
                }
            } else {
                showValidationErrorsForRoomRate(violations);
            }  
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        } catch (RoomTypeDNEException ex) {
            System.out.println("Room type does not exist!\n");
        }
    }
    
    public void doViewRoomRateDetails() {
        Scanner sc = new Scanner(System.in);
        String roomRateName;
        Integer response = 0;
        
        System.out.println("*** HoRS Management Client :: View Room Rate Details ***\n");
        System.out.print("Enter Room Rate Name> ");
        roomRateName = sc.nextLine().trim();
        try {
            RoomRate rr = roomRateSessionBeanRemote.retrieveRoomRateByRoomRateName(roomRateName);
            System.out.println(":: Details for Room Rate " + roomRateName + " ::");
            System.out.println("Name: " + rr.getRoomRateName());
            System.out.println("Rate Type: " + rr.getRateType().toString());
            System.out.println("Rate Per Night: " + rr.getRatePerNight().toString());
            if (rr.getRateType().equals(RateTypeEnum.PROMOTION) || rr.getRateType().equals(RateTypeEnum.PEAK)) {
                System.out.println("Rate Start Date: " + rr.getStartDate());
                System.out.println("Rate End Date: " + rr.getEndDate());
            }
            while (true) {
                System.out.println("\nFurther actions for Room Rate: " + roomRateName);
                System.out.println("1: Update Room Rate");
                System.out.println("2: Delete Room Rate");
                System.out.println("3: Go back");
                response = 0;
                while(response < 1 || response > 3) {
                    System.out.print("> ");
                    response = sc.nextInt();
                    
                    if (response == 1) {
                        doUpdateRoomRate(rr.getRoomRateName());
                    } else if (response == 2) {
                        doDeleteRoomRate(rr.getRoomRateName());
                        return;
                    } else if (response == 3) {
                        break;
                    } else {
                    System.out.println("Invalid option, please try again!\n");
                    }
                }
                if (response == 3) {
                    break;
                }
            }
        } catch (RoomRateDNEException ex) {
            System.out.println("Error while viewing room rate details. Room Rate " + roomRateName + " does not exist!\n");
        }
    }
    
    public void doUpdateRoomRate(String roomRateName) {
        Scanner sc = new Scanner(System.in);
        RoomRate newRoomRate = new RoomRate();
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("d/M/y");
            Date startDate;
            Date endDate;
        
            System.out.println("*** HoRS Management Client :: Update Existing Room Rate ***\n");
            System.out.println(":: Editing information for Room Rate: " + roomRateName);
            newRoomRate.setRoomRateName(roomRateName);
            System.out.print("Enter Rate Per Night> $");
            newRoomRate.setRatePerNight(BigDecimal.valueOf(sc.nextDouble()));
            sc.nextLine();
            
            while(true) {
                System.out.println("Select Rate Type:\n"
                        + "1: Published Rate\n"
                        + "2: Normal Rate\n"
                        + "3: Peak Rate\n"
                        + "4: Promotion Rate\n");
                System.out.print("> ");
                Integer rateTypeNum = sc.nextInt();

                if (rateTypeNum >= 1 && rateTypeNum <= 4) { //if within enum range
                    newRoomRate.setRateType(RateTypeEnum.values()[rateTypeNum - 1]);
                    sc.nextLine();
                    // if peak or promotion rate
                    if (rateTypeNum == 3 || rateTypeNum == 4) {
                        // set start and end date of rates
                        System.out.print("Enter Rate Start Date (dd/mm//yy): ");
                        startDate = inputDateFormat.parse(sc.nextLine().trim());
                        System.out.print("Enter Rate End Date (dd/mm//yy): ");
                        endDate = inputDateFormat.parse(sc.nextLine().trim()); 
                        newRoomRate.setStartDate(startDate);
                        newRoomRate.setEndDate(endDate);
                    }
                    break;
                } else {
                    System.out.println("Invalid option, please try again!\n");
                }
            }
            
            Set<ConstraintViolation<RoomRate>> violations = validator.validate(newRoomRate);
            if (violations.isEmpty()) {
                try {
                    roomRateSessionBeanRemote.updateRoomRate(roomRateName, newRoomRate);
                    System.out.println("RoomRate " + roomRateName + " successfully updated!");
                } catch (RoomRateDNEException ex) {
                    System.out.println(ex.getMessage() + "\n");
                }
            } else {
                showValidationErrorsForRoomRate(violations);
            }  
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
        }
   
    }
    
    public void doDeleteRoomRate(String roomRateName) {
        String confirmation = "";
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS Management Client :: Delete Existing RoomRate ***\n");
        System.out.print("Enter Name of RoomRate to Confirm Deletion> ");
        confirmation = sc.nextLine().trim();        
        if (confirmation.equals(roomRateName)) {
            try {
                roomRateSessionBeanRemote.deleteRoomRate(roomRateName);
                System.out.println("RoomRate: " + roomRateName + " successfully deleted!");
            } catch (RoomRateDNEException ex) {
                System.out.println(ex.getMessage());
            }
        } else {
            System.out.println("Invalid input! Name does not match RoomRate name to delete!");
        }
        
    }
    
    public void doViewAllRoomRates() {
        Scanner sc = new Scanner(System.in);
        System.out.println("*** HoRS Management Client :: View All Room Rates ***\n");
        
        List<RoomRate> roomRates = roomRateSessionBeanRemote.retrieveAllRoomRates(); 
        for (RoomRate rr : roomRates) {
            System.out.println("Name:" + rr.getRoomRateName() +
                    " | Rate Type: " + rr.getRateType().toString() +
                    " | Rate Per Night: " + rr.getRatePerNight().toString() + 
                    " | Rate Start Date: " + rr.getStartDate() + 
                    " | Rate End Date: " + rr.getEndDate() + 
                    " | isDisabled: " + rr.isDisabled());
        }
        System.out.print("Press ENTER to continue> ");
        sc.nextLine();
    }
    
    private void showValidationErrorsForRoomRate(Set<ConstraintViolation<RoomRate>> violations) {
        System.out.println("\n Input data validation error!");
        
        for (ConstraintViolation violation : violations) {
            System.out.println("\t" + violation.getPropertyPath() + "-" + violation.getInvalidValue() + "; " + violation.getMessage());
        }
        System.out.println("\nPlease try again!");
    }
}

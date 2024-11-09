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
import java.util.Scanner;
import util.enumeration.RateTypeEnum;
import util.exception.RoomRateExistsException;
import util.exception.RoomTypeDNEException;

/**
 *
 * @author yewkhang
 */
public class SalesModule {
    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;
    private RoomTypeSessionBeanRemote roomTypeSBRemote;
    private Staff currentStaff;

    public SalesModule(RoomRateSessionBeanRemote roomRateSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSBRemote, Staff currentStaff) {
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
            // how to associate???
            rt.getRoomRates().add(newRoomRate);
            
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
            
            try {
                Long roomRateId = roomRateSessionBeanRemote.createNewRoomRate(newRoomRate, rt.getRoomTypeName());
                System.out.println("New Room Rate Created: " + roomRateId + "\n");
            } catch (RoomRateExistsException ex) {
                System.out.println("Room rate already exists!");
            }
        } catch (ParseException ex) {
            System.out.println("Invalid date input!\n");
        } catch (RoomTypeDNEException ex) {
            System.out.println("Room type does not exist!\n");
        }
    }
    
    public void doViewRoomRateDetails() {
        
    }
    
    public void doUpdateRoomRate() {
        
    }
    
    public void doDeleteRoomRate() {
        
    }
    
    public void doViewAllRoomRates() {
        
    }
}

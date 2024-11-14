/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB31/SingletonEjbClass.java to edit this template
 */
package ejb.session.singleton;

import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import ejb.session.stateless.StaffSessionBeanLocal;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import entity.Staff;
import java.math.BigDecimal;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.enumeration.AccessRightEnum;
import util.enumeration.RateTypeEnum;
import util.exception.RoomDNEException;
import util.exception.RoomExistsException;
import util.exception.RoomRateDNEException;
import util.exception.RoomRateExistsException;
import util.exception.RoomTypeDNEException;
import util.exception.RoomTypeDisabledException;
import util.exception.RoomTypeExistsException;
import util.exception.StaffDNEException;
import util.exception.StaffUsernameExistsException;

/**
 *
 * @author yewkhang
 */
@Singleton
@LocalBean
@Startup
public class TestDataInitSessionBean {
    @EJB
    StaffSessionBeanLocal staffSessionBeanLocal;
    @EJB
    RoomTypeSessionBeanLocal roomTypeSessionBeanLocal;
    @EJB
    RoomRateSessionBeanLocal roomRateSessionBeanLocal;
    @EJB
    RoomSessionBeanLocal roomSessionBeanLocal;

    public TestDataInitSessionBean() {
    }
    
    @PostConstruct
    public void postConstruct() {
        try {
            staffSessionBeanLocal.retrieveStaffByUsername("sysadmin");
        } catch (StaffDNEException ex) {
            loadEmployeeTestData();
        } 
        
        try {
            roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName("Grand Suite");
            System.out.println("test room type");
        } catch (RoomTypeDNEException ex) {
            loadRoomTypeTestData();
        }
        
        try {
            roomRateSessionBeanLocal.retrieveRoomRateByRoomRateName("Deluxe Room Published");
            System.out.println("test room rate");
        } catch (RoomRateDNEException ex) {
            loadRoomRateTestData();
        }
        
        try {
            roomSessionBeanLocal.retrieveRoomByRoomNumber("0101");
            System.out.println("test room");
        } catch (RoomDNEException ex) {
            loadRoomTestData();
        }
    }
    
    private void loadEmployeeTestData() {
        try {
            staffSessionBeanLocal.createNewStaff(new Staff("sysadmin", "password", AccessRightEnum.ADMINISTRATOR));
            staffSessionBeanLocal.createNewStaff(new Staff("opmanager", "password", AccessRightEnum.OPERATIONS));
            staffSessionBeanLocal.createNewStaff(new Staff("salesmanager", "password", AccessRightEnum.SALES));
            staffSessionBeanLocal.createNewStaff(new Staff("guestrelo", "password", AccessRightEnum.RELATIONS));
        } catch (StaffUsernameExistsException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadRoomTypeTestData() {
        try {
            roomTypeSessionBeanLocal.createNewRoomType(new RoomType("Grand Suite", "description", 10, 2, 2, "fridge,sofa", false));
            roomTypeSessionBeanLocal.createNewRoomType(new RoomType("Junior Suite", "description", 10, 2, 2, "fridge,sofa", false));
            roomTypeSessionBeanLocal.setNextHigherRoomType("Junior Suite", "Grand Suite");
            
            roomTypeSessionBeanLocal.createNewRoomType(new RoomType("Family Room", "description", 10, 2, 2, "fridge,sofa", false));
            roomTypeSessionBeanLocal.setNextHigherRoomType("Family Room", "Junior Suite");
            
            roomTypeSessionBeanLocal.createNewRoomType(new RoomType("Premier Room", "description", 10, 2, 2, "fridge,sofa", false));
            roomTypeSessionBeanLocal.setNextHigherRoomType("Premier Room", "Family Room");
            
            roomTypeSessionBeanLocal.createNewRoomType(new RoomType("Deluxe Room", "description", 10, 2, 2, "fridge,sofa", false));
            roomTypeSessionBeanLocal.setNextHigherRoomType("Deluxe Room", "Premier Room");
        } catch (RoomTypeExistsException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadRoomRateTestData() {
        try {
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Deluxe Room Published", RateTypeEnum.PUBLISHED, BigDecimal.valueOf(100), false), "Deluxe Room");
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Deluxe Room Normal", RateTypeEnum.NORMAL, BigDecimal.valueOf(50), false), "Deluxe Room");
            
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Premier Room Published", RateTypeEnum.PUBLISHED, BigDecimal.valueOf(200), false), "Premier Room");
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Premier Room Normal", RateTypeEnum.NORMAL, BigDecimal.valueOf(100), false), "Premier Room");
            
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Family Room Published", RateTypeEnum.PUBLISHED, BigDecimal.valueOf(300), false), "Family Room");
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Family Room Normal", RateTypeEnum.NORMAL, BigDecimal.valueOf(150), false), "Family Room");
            
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Junior Suite Published", RateTypeEnum.PUBLISHED, BigDecimal.valueOf(400), false), "Junior Suite");
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Junior Suite Normal", RateTypeEnum.NORMAL, BigDecimal.valueOf(200), false), "Junior Suite");
            
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Grand Suite Published", RateTypeEnum.PUBLISHED, BigDecimal.valueOf(500), false), "Grand Suite");
            roomRateSessionBeanLocal.createNewRoomRate(new RoomRate("Grand Suite Normal", RateTypeEnum.NORMAL, BigDecimal.valueOf(250), false), "Grand Suite");
        } catch (RoomRateExistsException | RoomTypeDNEException | RoomTypeDisabledException ex) {
            ex.printStackTrace();
        }
    }
    
    private void loadRoomTestData() {
        try {
            RoomType deluxeRoom = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName("Deluxe Room");
            Room room0101 = new Room("0101", true);
            room0101.setRoomType(deluxeRoom);
            Room room0201 = new Room("0201", true);
            room0201.setRoomType(deluxeRoom);
            Room room0301 = new Room("0301", true);
            room0301.setRoomType(deluxeRoom);
            Room room0401 = new Room("0401", true);
            room0401.setRoomType(deluxeRoom);
            Room room0501 = new Room("0501", true);
            room0501.setRoomType(deluxeRoom);
            roomSessionBeanLocal.createNewRoom(room0101, "Deluxe Room");
            roomSessionBeanLocal.createNewRoom(room0201, "Deluxe Room");
            roomSessionBeanLocal.createNewRoom(room0301, "Deluxe Room");
            roomSessionBeanLocal.createNewRoom(room0401, "Deluxe Room");
            roomSessionBeanLocal.createNewRoom(room0501, "Deluxe Room");
            
            RoomType premierRoom = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName("Premier Room");
            Room room0102 = new Room("0102", true);
            room0102.setRoomType(premierRoom);
            Room room0202 = new Room("0202", true);
            room0202.setRoomType(premierRoom);
            Room room0302 = new Room("0302", true);
            room0302.setRoomType(premierRoom);
            Room room0402 = new Room("0402", true);
            room0402.setRoomType(premierRoom);
            Room room0502 = new Room("0502", true);
            room0502.setRoomType(premierRoom);
            roomSessionBeanLocal.createNewRoom(room0102, "Premier Room");
            roomSessionBeanLocal.createNewRoom(room0202, "Premier Room");
            roomSessionBeanLocal.createNewRoom(room0302, "Premier Room");
            roomSessionBeanLocal.createNewRoom(room0402, "Premier Room");
            roomSessionBeanLocal.createNewRoom(room0502, "Premier Room");
            
            RoomType family = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName("Family Room");
            Room room0103 = new Room("0103", true);
            room0103.setRoomType(family);
            Room room0203 = new Room("0203", true);
            room0203.setRoomType(family);
            Room room0303 = new Room("0303", true);
            room0303.setRoomType(family);
            Room room0403 = new Room("0403", true);
            room0403.setRoomType(family);
            Room room0503 = new Room("0503", true);
            room0503.setRoomType(family);
            roomSessionBeanLocal.createNewRoom(room0103, "Family Room");
            roomSessionBeanLocal.createNewRoom(room0203, "Family Room");
            roomSessionBeanLocal.createNewRoom(room0303, "Family Room");
            roomSessionBeanLocal.createNewRoom(room0403, "Family Room");
            roomSessionBeanLocal.createNewRoom(room0503, "Family Room");
            
            RoomType junior = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName("Junior Suite");
            Room room0104 = new Room("0104", true);
            room0104.setRoomType(junior);
            Room room0204 = new Room("0204", true);
            room0204.setRoomType(junior);
            Room room0304 = new Room("0304", true);
            room0304.setRoomType(junior);
            Room room0404 = new Room("0404", true);
            room0404.setRoomType(junior);
            Room room0504 = new Room("0504", true);
            room0504.setRoomType(junior);
            roomSessionBeanLocal.createNewRoom(room0104, "Junior Suite");
            roomSessionBeanLocal.createNewRoom(room0204, "Junior Suite");
            roomSessionBeanLocal.createNewRoom(room0304, "Junior Suite");
            roomSessionBeanLocal.createNewRoom(room0404, "Junior Suite");
            roomSessionBeanLocal.createNewRoom(room0504, "Junior Suite");
            
            RoomType grand = roomTypeSessionBeanLocal.retrieveRoomTypeByRoomTypeName("Grand Suite");
            Room room0105 = new Room("0105", true);
            room0105.setRoomType(grand);
            Room room0205 = new Room("0205", true);
            room0205.setRoomType(grand);
            Room room0305 = new Room("0305", true);
            room0305.setRoomType(grand);
            Room room0405 = new Room("0405", true);
            room0405.setRoomType(grand);
            Room room0505 = new Room("0505", true);
            room0505.setRoomType(grand);
            roomSessionBeanLocal.createNewRoom(room0105, "Grand Suite");
            roomSessionBeanLocal.createNewRoom(room0205, "Grand Suite");
            roomSessionBeanLocal.createNewRoom(room0305, "Grand Suite");
            roomSessionBeanLocal.createNewRoom(room0405, "Grand Suite");
            roomSessionBeanLocal.createNewRoom(room0505, "Grand Suite");
        } catch (RoomExistsException | RoomTypeDNEException | RoomTypeDisabledException ex) {
            ex.printStackTrace();
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Guest;
import java.util.List;
import javax.ejb.Remote;
import util.exception.GuestDNEException;
import util.exception.GuestExistsException;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author wallace
 */
@Remote
public interface GuestSessionBeanRemote {
    public Long createNewGuest(Guest guest) throws GuestExistsException;
    
    public List<Guest> retrieveAllGuests();
    
    public Guest retrieveGuestByGuestId(Long guestId) throws GuestDNEException;
    
    public Guest retrieveGuestByEmail(String email) throws GuestDNEException;
    
    public Guest guestLogin(String email, String password) throws InvalidLoginCredentialException, GuestDNEException;
}

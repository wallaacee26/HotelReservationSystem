/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/EjbWebService.java to edit this template
 */
package ejb.session.ws;

import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.Partner;
import entity.Reservation;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerDNEException;
import util.exception.PartnerExistsException;

/**
 *
 * @author wallace
 */
@WebService(serviceName = "PartnerWebService")
@Stateless()
public class PartnerWebService {
    
    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;
    
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @WebMethod(operationName = "createNewPartner")
    public Long createNewPartner(@WebParam(name = "partner") Partner partner) throws PartnerExistsException {
        try {
            return partnerSessionBeanLocal.createNewPartner(partner);
        } catch(PersistenceException ex) {
           if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new PartnerExistsException();
                } else {
                    throw new PartnerExistsException(ex.getMessage());
                }
            } else {
               throw new PartnerExistsException(ex.getMessage());
           }
        }
    }
    
    @WebMethod(operationName = "retrieveAllPartners")
    public List<Partner> retrieveAllPartners() {
        
        List<Partner> partners = partnerSessionBeanLocal.retrieveAllPartners();
        for (Partner partner : partners) {
            em.detach(partner);
            for (Reservation reservation : partner.getReservations()) {
                em.detach(reservation);
                reservation.setCustomerOrGuest(null);
                reservation.setPartner(null);
                reservation.getReservedRooms().clear();
            }
        }
        return partners;
    }
    
    @WebMethod(operationName = "retrievePartnerByPartnerId")
    public Partner retrievePartnerByPartnerId(@WebParam(name = "partnerId") Long partnerId) throws PartnerDNEException {
        Partner partner = partnerSessionBeanLocal.retrievePartnerByPartnerId(partnerId);
        em.detach(partner);
        for (Reservation reservation : partner.getReservations()) {
            em.detach(reservation);
            reservation.setCustomerOrGuest(null);
            reservation.setPartner(null);
            reservation.getReservedRooms().clear();
        }
        return partner;
    }
    
    @WebMethod(operationName = "retrievePartnerByUsername")
    public Partner retrievePartnerByUsername(@WebParam(name = "username") String username) throws PartnerDNEException {
        Partner partner = partnerSessionBeanLocal.retrievePartnerByUsername(username);
        em.detach(partner);
        for (Reservation reservation : partner.getReservations()) {
            em.detach(reservation);
            reservation.setCustomerOrGuest(null);
            reservation.setPartner(null);
            reservation.getReservedRooms().clear();
        }
        return partner;
    }
    
    @WebMethod(operationName = "partnerLogin")
    public Partner partnerLogin(@WebParam(name = "username") String username, @WebParam(name = "password") String password)
            throws InvalidLoginCredentialException, PartnerDNEException {
        Partner partner = partnerSessionBeanLocal.partnerLogin(username, password);
        em.detach(partner);
        for (Reservation reservation : partner.getReservations()) {
            em.detach(reservation);
            reservation.setCustomerOrGuest(null);
            reservation.setPartner(null);
            reservation.getReservedRooms().clear();
        }
        return partner;
    }
}

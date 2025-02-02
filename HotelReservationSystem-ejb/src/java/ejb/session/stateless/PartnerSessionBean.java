/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/StatelessEjbClass.java to edit this template
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerDNEException;
import util.exception.PartnerExistsException;

/**
 *
 * @author yewkhang
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewPartner(Partner partner) throws PartnerExistsException {
        try {
            em.persist(partner);
            em.flush();
            return partner.getPartnerId();
        } catch(PersistenceException ex) {
           if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
            {
                if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                {
                    throw new PartnerExistsException();
                } else {
                    throw new PartnerExistsException(ex.getMessage()); // UnknownPersistenceException, but just using this exception for simplicity
                }
            } else {
               throw new PartnerExistsException(ex.getMessage()); // UnknownPersistenceException, but just using this exception for simplicity
           }
        }
    }
    
    @Override
    public List<Partner> retrieveAllPartners() {
        Query query = em.createQuery("SELECT p from Partner p");
        List<Partner> partners = query.getResultList();
        for (Partner partner : partners) {
            partner.getReservations().size();
        }
        return partners;
    }
    
    @Override
    public Partner retrievePartnerByPartnerId(Long partnerId) throws PartnerDNEException {
        Partner partner = em.find(Partner.class, partnerId); 
        
        if (partner != null) {
            partner.getReservations().size(); // trigger lazy fetching
            return partner;
        } else {
            throw new PartnerDNEException("Partner does not exist: " + partnerId);
        }
    }
    
    @Override
    public Partner retrievePartnerByUsername(String username) throws PartnerDNEException {
        Query query = em.createQuery("SELECT p from Partner p WHERE p.username = :inUsername");
        query.setParameter("inUsername", username);
        
        try {
            return (Partner)query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new PartnerDNEException("Partner username " + username + " does not exist!");
        }
    }
    
    @Override
    public Partner partnerLogin(String username, String password) throws InvalidLoginCredentialException, PartnerDNEException {
        try {
            Partner p = retrievePartnerByUsername(username);
            
            if (p.getPassword().equals(password)) {
                // preload any lazy data if needed
                p.getReservations().size(); // trigger lazy fetching
                return p;
            } else {
                throw new InvalidLoginCredentialException("Partner username or password is incorrect!");
            }
        } catch (PartnerDNEException ex) {
            throw new PartnerDNEException("Partner username does not exist!");
        }
    }
}

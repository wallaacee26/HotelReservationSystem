/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB30/SessionRemote.java to edit this template
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Remote;
import util.exception.PartnerExistsException;

/**
 *
 * @author yewkhang
 */
@Remote
public interface PartnerSessionBeanRemote {

    public Long createNewPartner(Partner partner) throws PartnerExistsException;

    public List<Partner> retrieveAllPartners();
    
}

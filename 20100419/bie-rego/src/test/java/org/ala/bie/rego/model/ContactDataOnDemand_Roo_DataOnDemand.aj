package org.ala.bie.rego.model;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import org.ala.bie.rego.model.Contact;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

privileged aspect ContactDataOnDemand_Roo_DataOnDemand {
    
    declare @type: ContactDataOnDemand: @Component;    
    
    private Random ContactDataOnDemand.rnd = new SecureRandom();    
    
    private List<Contact> ContactDataOnDemand.data;    
    
    public Contact ContactDataOnDemand.getNewTransientContact(int index) {    
        org.ala.bie.rego.model.Contact obj = new org.ala.bie.rego.model.Contact();        
        obj.setEmail("email_" + index);        
        obj.setName("name_" + index);        
        obj.setPhone("phone_" + index);        
        return obj;        
    }    
    
    public Contact ContactDataOnDemand.getRandomContact() {    
        init();        
        Contact obj = data.get(rnd.nextInt(data.size()));        
        return Contact.findContact(obj.getId());        
    }    
    
    public boolean ContactDataOnDemand.modifyContact(Contact obj) {    
        return false;        
    }    
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)    
    public void ContactDataOnDemand.init() {    
        if (data != null) {        
            return;            
        }        
                
        data = org.ala.bie.rego.model.Contact.findContactEntries(0, 10);        
        if (data == null) throw new IllegalStateException("Find entries implementation for 'Contact' illegally returned null");        
        if (data.size() > 0) {        
            return;            
        }        
                
        data = new java.util.ArrayList<org.ala.bie.rego.model.Contact>();        
        for (int i = 0; i < 10; i++) {        
            org.ala.bie.rego.model.Contact obj = getNewTransientContact(i);            
            obj.persist();            
            data.add(obj);            
        }        
    }    
    
}

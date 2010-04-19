package org.ala.bie.rego.model;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import org.ala.bie.rego.model.Infosource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

privileged aspect InfosourceDataOnDemand_Roo_DataOnDemand {
    
    declare @type: InfosourceDataOnDemand: @Component;    
    
    private Random InfosourceDataOnDemand.rnd = new SecureRandom();    
    
    private List<Infosource> InfosourceDataOnDemand.data;    
    
    public Infosource InfosourceDataOnDemand.getNewTransientInfosource(int index) {    
        org.ala.bie.rego.model.Infosource obj = new org.ala.bie.rego.model.Infosource();        
        obj.setAcronym("acronym_" + index);        
        obj.setDescription("description_" + index);        
        obj.setName("name_" + index);        
        return obj;        
    }    
    
    public Infosource InfosourceDataOnDemand.getRandomInfosource() {    
        init();        
        Infosource obj = data.get(rnd.nextInt(data.size()));        
        return Infosource.findInfosource(obj.getId());        
    }    
    
    public boolean InfosourceDataOnDemand.modifyInfosource(Infosource obj) {    
        return false;        
    }    
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)    
    public void InfosourceDataOnDemand.init() {    
        if (data != null) {        
            return;            
        }        
                
        data = org.ala.bie.rego.model.Infosource.findInfosourceEntries(0, 10);        
        if (data == null) throw new IllegalStateException("Find entries implementation for 'Infosource' illegally returned null");        
        if (data.size() > 0) {        
            return;            
        }        
                
        data = new java.util.ArrayList<org.ala.bie.rego.model.Infosource>();        
        for (int i = 0; i < 10; i++) {        
            org.ala.bie.rego.model.Infosource obj = getNewTransientInfosource(i);            
            obj.persist();            
            data.add(obj);            
        }        
    }    
    
}

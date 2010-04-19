package org.ala.bie.rego.model;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import org.ala.bie.rego.model.ScientificName;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

privileged aspect ScientificNameDataOnDemand_Roo_DataOnDemand {
    
    declare @type: ScientificNameDataOnDemand: @Component;    
    
    private Random ScientificNameDataOnDemand.rnd = new SecureRandom();    
    
    private List<ScientificName> ScientificNameDataOnDemand.data;    
    
    public ScientificName ScientificNameDataOnDemand.getNewTransientScientificName(int index) {    
        org.ala.bie.rego.model.ScientificName obj = new org.ala.bie.rego.model.ScientificName();        
        obj.setCommonName("commonName_" + index);        
        obj.setName("name_" + index);        
        obj.setRank("rank_" + index);        
        return obj;        
    }    
    
    public ScientificName ScientificNameDataOnDemand.getRandomScientificName() {    
        init();        
        ScientificName obj = data.get(rnd.nextInt(data.size()));        
        return ScientificName.findScientificName(obj.getId());        
    }    
    
    public boolean ScientificNameDataOnDemand.modifyScientificName(ScientificName obj) {    
        return false;        
    }    
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)    
    public void ScientificNameDataOnDemand.init() {    
        if (data != null) {        
            return;            
        }        
                
        data = org.ala.bie.rego.model.ScientificName.findScientificNameEntries(0, 10);        
        if (data == null) throw new IllegalStateException("Find entries implementation for 'ScientificName' illegally returned null");        
        if (data.size() > 0) {        
            return;            
        }        
                
        data = new java.util.ArrayList<org.ala.bie.rego.model.ScientificName>();        
        for (int i = 0; i < 10; i++) {        
            org.ala.bie.rego.model.ScientificName obj = getNewTransientScientificName(i);            
            obj.persist();            
            data.add(obj);            
        }        
    }    
    
}

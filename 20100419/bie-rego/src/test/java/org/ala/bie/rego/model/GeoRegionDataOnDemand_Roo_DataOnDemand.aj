package org.ala.bie.rego.model;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import org.ala.bie.rego.model.GeoRegion;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

privileged aspect GeoRegionDataOnDemand_Roo_DataOnDemand {
    
    declare @type: GeoRegionDataOnDemand: @Component;    
    
    private Random GeoRegionDataOnDemand.rnd = new SecureRandom();    
    
    private List<GeoRegion> GeoRegionDataOnDemand.data;    
    
    public GeoRegion GeoRegionDataOnDemand.getNewTransientGeoRegion(int index) {    
        org.ala.bie.rego.model.GeoRegion obj = new org.ala.bie.rego.model.GeoRegion();        
        obj.setName("name_" + index);        
        obj.setRegionType("regionType_" + index);        
        return obj;        
    }    
    
    public GeoRegion GeoRegionDataOnDemand.getRandomGeoRegion() {    
        init();        
        GeoRegion obj = data.get(rnd.nextInt(data.size()));        
        return GeoRegion.findGeoRegion(obj.getId());        
    }    
    
    public boolean GeoRegionDataOnDemand.modifyGeoRegion(GeoRegion obj) {    
        return false;        
    }    
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)    
    public void GeoRegionDataOnDemand.init() {    
        if (data != null) {        
            return;            
        }        
                
        data = org.ala.bie.rego.model.GeoRegion.findGeoRegionEntries(0, 10);        
        if (data == null) throw new IllegalStateException("Find entries implementation for 'GeoRegion' illegally returned null");        
        if (data.size() > 0) {        
            return;            
        }        
                
        data = new java.util.ArrayList<org.ala.bie.rego.model.GeoRegion>();        
        for (int i = 0; i < 10; i++) {        
            org.ala.bie.rego.model.GeoRegion obj = getNewTransientGeoRegion(i);            
            obj.persist();            
            data.add(obj);            
        }        
    }    
    
}

package org.ala.bie.rego.model;

import java.lang.String;

privileged aspect GeoRegion_Roo_JavaBean {
    
    public String GeoRegion.getName() {    
        return this.name;        
    }    
    
    public void GeoRegion.setName(String name) {    
        this.name = name;        
    }    
    
    public String GeoRegion.getRegionType() {    
        return this.regionType;        
    }    
    
    public void GeoRegion.setRegionType(String regionType) {    
        this.regionType = regionType;        
    }    
    
}

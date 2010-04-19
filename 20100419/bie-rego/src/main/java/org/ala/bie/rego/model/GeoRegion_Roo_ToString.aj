package org.ala.bie.rego.model;

import java.lang.String;

privileged aspect GeoRegion_Roo_ToString {
    
    public String GeoRegion.toString() {    
        StringBuilder sb = new StringBuilder();        
        sb.append(getName()).append(" - ").append(getRegionType());        
        return sb.toString();        
    }    
}

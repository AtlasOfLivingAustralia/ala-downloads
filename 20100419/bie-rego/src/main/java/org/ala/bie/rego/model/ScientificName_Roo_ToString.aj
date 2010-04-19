package org.ala.bie.rego.model;

import java.lang.String;

privileged aspect ScientificName_Roo_ToString {
    
    public String ScientificName.toString() {    
        StringBuilder sb = new StringBuilder();        
        sb.append(getName());    
        if(getCommonName()!=null && getCommonName().length()>0){
        	sb.append(" (").append(getCommonName()).append(")");
        }
        return sb.toString();        
    }    
    
}

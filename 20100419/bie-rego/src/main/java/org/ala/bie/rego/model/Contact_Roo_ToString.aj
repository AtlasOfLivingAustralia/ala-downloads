package org.ala.bie.rego.model;

import java.lang.String;

privileged aspect Contact_Roo_ToString {
    
    public String Contact.toString() {    
        StringBuilder sb = new StringBuilder();        
        sb.append("Id: ").append(getId()).append(", ");        
        sb.append("Version: ").append(getVersion()).append(", ");        
        sb.append("Name: ").append(getName()).append(", ");        
        sb.append("Email: ").append(getEmail()).append(", ");        
        sb.append("Phone: ").append(getPhone());        
        return sb.toString();        
    }    
    
}

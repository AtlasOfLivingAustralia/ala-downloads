package org.ala.bie.rego.model;

import java.lang.String;

privileged aspect Contact_Roo_JavaBean {
    
    public String Contact.getName() {    
        return this.name;        
    }    
    
    public void Contact.setName(String name) {    
        this.name = name;        
    }    
    
    public String Contact.getEmail() {    
        return this.email;        
    }    
    
    public void Contact.setEmail(String email) {    
        this.email = email;        
    }    
    
    public String Contact.getPhone() {    
        return this.phone;        
    }    
    
    public void Contact.setPhone(String phone) {    
        this.phone = phone;        
    }    
    
}

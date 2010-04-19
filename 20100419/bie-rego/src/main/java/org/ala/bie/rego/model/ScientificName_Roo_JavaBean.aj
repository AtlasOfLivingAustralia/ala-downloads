package org.ala.bie.rego.model;

import java.lang.String;

privileged aspect ScientificName_Roo_JavaBean {
    
    public String ScientificName.getName() {    
        return this.name;        
    }    
    
    public void ScientificName.setName(String name) {    
        this.name = name;        
    }    
    
    public String ScientificName.getRank() {    
        return this.rank;        
    }    
    
    public void ScientificName.setRank(String rank) {    
        this.rank = rank;        
    }    
    
    public String ScientificName.getCommonName() {    
        return this.commonName;        
    }    
    
    public void ScientificName.setCommonName(String commonName) {    
        this.commonName = commonName;        
    }    
    
}

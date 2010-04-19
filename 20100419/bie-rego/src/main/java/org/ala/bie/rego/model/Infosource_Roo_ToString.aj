package org.ala.bie.rego.model;

import java.lang.String;

privileged aspect Infosource_Roo_ToString {
    
    public String Infosource.toString() {    
        StringBuilder sb = new StringBuilder();        
        sb.append("Id: ").append(getId()).append(", ");        
        sb.append("Version: ").append(getVersion()).append(", ");        
        sb.append("Name: ").append(getName()).append(", ");        
        sb.append("Description: ").append(getDescription()).append(", ");        
        sb.append("Acronym: ").append(getAcronym()).append(", ");        
        sb.append("Uri: ").append(getUri()).append(", ");        
        sb.append("WebsiteUrl: ").append(getWebsiteUrl()).append(", ");        
        sb.append("LogoUrl: ").append(getLogoUrl()).append(", ");        
        sb.append("GeographicDescription: ").append(getGeographicDescription()).append(", ");        
        sb.append("States: ").append(getStates() == null ? "null" : getStates().size()).append(", ");        
        sb.append("WellKnownText: ").append(getWellKnownText()).append(", ");        
        sb.append("NorthCoordinate: ").append(getNorthCoordinate()).append(", ");        
        sb.append("SouthCoordinate: ").append(getSouthCoordinate()).append(", ");        
        sb.append("EastCoordinate: ").append(getEastCoordinate()).append(", ");        
        sb.append("WestCoordinate: ").append(getWestCoordinate()).append(", ");        
        sb.append("SingleDate: ").append(getSingleDate()).append(", ");        
        sb.append("StartDate: ").append(getStartDate()).append(", ");        
        sb.append("EndDate: ").append(getEndDate()).append(", ");        
        sb.append("ScientificNames: ").append(getScientificNames() == null ? "null" : getScientificNames().size()).append(", ");        
        sb.append("DocumentMapper: ").append(getDocumentMapper()).append(", ");        
        sb.append("HarvesterId: ").append(getHarvesterId()).append(", ");        
        sb.append("ConnectionParams: ").append(getConnectionParams());        
        return sb.toString();        
    }    
    
}

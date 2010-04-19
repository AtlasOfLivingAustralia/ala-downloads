package org.ala.bie.rego.model;

import java.lang.Float;
import java.lang.String;
import java.util.Date;
import java.util.Set;
import org.ala.bie.rego.model.GeoRegion;
import org.ala.bie.rego.model.ScientificName;

privileged aspect Infosource_Roo_JavaBean {
    
    public String Infosource.getName() {    
        return this.name;        
    }    
    
    public void Infosource.setName(String name) {    
        this.name = name;        
    }    
    
    public String Infosource.getDescription() {    
        return this.description;        
    }    
    
    public void Infosource.setDescription(String description) {    
        this.description = description;        
    }    
    
    public String Infosource.getAcronym() {    
        return this.acronym;        
    }    
    
    public void Infosource.setAcronym(String acronym) {    
        this.acronym = acronym;        
    }    
    
    public String Infosource.getUri() {    
        return this.uri;        
    }    
    
    public void Infosource.setUri(String uri) {    
        this.uri = uri;        
    }    
    
    public String Infosource.getWebsiteUrl() {    
        return this.websiteUrl;        
    }    
    
    public void Infosource.setWebsiteUrl(String websiteUrl) {    
        this.websiteUrl = websiteUrl;        
    }    
    
    public String Infosource.getLogoUrl() {    
        return this.logoUrl;        
    }    
    
    public void Infosource.setLogoUrl(String logoUrl) {    
        this.logoUrl = logoUrl;        
    }    
    
    public String Infosource.getGeographicDescription() {    
        return this.geographicDescription;        
    }    
    
    public void Infosource.setGeographicDescription(String geographicDescription) {    
        this.geographicDescription = geographicDescription;        
    }    
    
    public Set<GeoRegion> Infosource.getStates() {    
        return this.states;        
    }    
    
    public void Infosource.setStates(Set<GeoRegion> states) {    
        this.states = states;        
    }    
    
    public String Infosource.getWellKnownText() {    
        return this.wellKnownText;        
    }    
    
    public void Infosource.setWellKnownText(String wellKnownText) {    
        this.wellKnownText = wellKnownText;        
    }    
    
    public Float Infosource.getNorthCoordinate() {    
        return this.northCoordinate;        
    }    
    
    public void Infosource.setNorthCoordinate(Float northCoordinate) {    
        this.northCoordinate = northCoordinate;        
    }    
    
    public Float Infosource.getSouthCoordinate() {    
        return this.southCoordinate;        
    }    
    
    public void Infosource.setSouthCoordinate(Float southCoordinate) {    
        this.southCoordinate = southCoordinate;        
    }    
    
    public Float Infosource.getEastCoordinate() {    
        return this.eastCoordinate;        
    }    
    
    public void Infosource.setEastCoordinate(Float eastCoordinate) {    
        this.eastCoordinate = eastCoordinate;        
    }    
    
    public Float Infosource.getWestCoordinate() {    
        return this.westCoordinate;        
    }    
    
    public void Infosource.setWestCoordinate(Float westCoordinate) {    
        this.westCoordinate = westCoordinate;        
    }    
    
    public Date Infosource.getSingleDate() {    
        return this.singleDate;        
    }    
    
    public void Infosource.setSingleDate(Date singleDate) {    
        this.singleDate = singleDate;        
    }    
    
    public Date Infosource.getStartDate() {    
        return this.startDate;        
    }    
    
    public void Infosource.setStartDate(Date startDate) {    
        this.startDate = startDate;        
    }    
    
    public Date Infosource.getEndDate() {    
        return this.endDate;        
    }    
    
    public void Infosource.setEndDate(Date endDate) {    
        this.endDate = endDate;        
    }    
    
    public Set<ScientificName> Infosource.getScientificNames() {    
        return this.scientificNames;        
    }    
    
    public void Infosource.setScientificNames(Set<ScientificName> scientificNames) {    
        this.scientificNames = scientificNames;        
    }    
    
    public String Infosource.getDocumentMapper() {    
        return this.documentMapper;        
    }    
    
    public void Infosource.setDocumentMapper(String documentMapper) {    
        this.documentMapper = documentMapper;        
    }    
    
    public String Infosource.getHarvesterId() {    
        return this.harvesterId;        
    }    
    
    public void Infosource.setHarvesterId(String harvesterId) {    
        this.harvesterId = harvesterId;        
    }    
    
    public String Infosource.getConnectionParams() {    
        return this.connectionParams;        
    }    
    
    public void Infosource.setConnectionParams(String connectionParams) {    
        this.connectionParams = connectionParams;        
    }    
    
}

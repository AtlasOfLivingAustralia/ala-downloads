package org.ala.bie.rego.model;



@javax.persistence.Entity
@org.springframework.roo.addon.javabean.RooJavaBean
@org.springframework.roo.addon.tostring.RooToString
@org.springframework.roo.addon.entity.RooEntity(identifierField = "id", identifierColumn = "id")
@javax.persistence.Table(name = "infosource")

public class Infosource {

    @javax.validation.constraints.NotNull
    @javax.validation.constraints.Size(min = 2)
    private String name;

    private String description;

    private String acronym;

    @javax.persistence.Column(name = "uri")
    private String uri;

    @javax.persistence.Column(name = "website_url")
    private String websiteUrl;

    @javax.persistence.Column(name = "logo_url")
    private String logoUrl;

    @javax.persistence.Column(name = "geographic_description")
    private String geographicDescription;

    @javax.persistence.ManyToMany(cascade = javax.persistence.CascadeType.ALL)
    private java.util.Set<org.ala.bie.rego.model.GeoRegion> states = new java.util.HashSet<org.ala.bie.rego.model.GeoRegion>();

    @javax.persistence.Column(name = "well_known_text")
    private String wellKnownText;

    @javax.persistence.Column(name = "north_coordinate")
    private Float northCoordinate;

    @javax.persistence.Column(name = "south_coordinate")
    private Float southCoordinate;

    @javax.persistence.Column(name = "east_coordinate")
    private Float eastCoordinate;

    @javax.persistence.Column(name = "west_coordinate")
    private Float westCoordinate;

    @javax.persistence.Column(name = "single_date")
    @javax.validation.constraints.Past
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @org.springframework.format.annotation.DateTimeFormat(style = "S-")
    private java.util.Date singleDate;

    @javax.persistence.Column(name = "start_date")
    @javax.validation.constraints.Past
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @org.springframework.format.annotation.DateTimeFormat(style = "S-")
    private java.util.Date startDate;

    @javax.persistence.Column(name = "end_date")
    @javax.persistence.Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @org.springframework.format.annotation.DateTimeFormat(style = "S-")
    private java.util.Date endDate;

    @javax.persistence.ManyToMany(cascade = javax.persistence.CascadeType.ALL)
    private java.util.Set<org.ala.bie.rego.model.ScientificName> scientificNames = new java.util.HashSet<org.ala.bie.rego.model.ScientificName>();

    @javax.persistence.Column(name = "document_mapper")
    private String documentMapper;

    @javax.persistence.Column(name = "harvester_id")
    private String harvesterId;

    @javax.persistence.Column(name = "connection_params")
    private String connectionParams;
}

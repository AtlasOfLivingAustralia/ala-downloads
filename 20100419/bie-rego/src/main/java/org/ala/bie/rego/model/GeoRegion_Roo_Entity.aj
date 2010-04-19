package org.ala.bie.rego.model;

import java.lang.Integer;
import java.lang.Long;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Version;
import org.ala.bie.rego.model.GeoRegion;
import org.springframework.transaction.annotation.Transactional;

privileged aspect GeoRegion_Roo_Entity {
    
    @PersistenceContext    
    transient EntityManager GeoRegion.entityManager;    
    
    @Id    
    @GeneratedValue(strategy = GenerationType.AUTO)    
    @Column(name = "id")    
    private Long GeoRegion.id;    
    
    @Version    
    @Column(name = "version")    
    private Integer GeoRegion.version;    
    
    public Long GeoRegion.getId() {    
        return this.id;        
    }    
    
    public void GeoRegion.setId(Long id) {    
        this.id = id;        
    }    
    
    public Integer GeoRegion.getVersion() {    
        return this.version;        
    }    
    
    public void GeoRegion.setVersion(Integer version) {    
        this.version = version;        
    }    
    
    @Transactional    
    public void GeoRegion.persist() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        this.entityManager.persist(this);        
    }    
    
    @Transactional    
    public void GeoRegion.remove() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        if (this.entityManager.contains(this)) {        
            this.entityManager.remove(this);            
        } else {        
            GeoRegion attached = this.entityManager.find(GeoRegion.class, this.id);            
            this.entityManager.remove(attached);            
        }        
    }    
    
    @Transactional    
    public void GeoRegion.flush() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        this.entityManager.flush();        
    }    
    
    @Transactional    
    public void GeoRegion.merge() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        GeoRegion merged = this.entityManager.merge(this);        
        this.entityManager.flush();        
        this.id = merged.getId();        
    }    
    
    public static final EntityManager GeoRegion.entityManager() {    
        EntityManager em = new GeoRegion().entityManager;        
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");        
        return em;        
    }    
    
    public static long GeoRegion.countGeoRegions() {    
        return (Long) entityManager().createQuery("select count(o) from GeoRegion o").getSingleResult();        
    }    
    
    public static List<GeoRegion> GeoRegion.findAllGeoRegions() {    
        return entityManager().createQuery("select o from GeoRegion o").getResultList();        
    }    
    
    public static GeoRegion GeoRegion.findGeoRegion(Long id) {    
        if (id == null) throw new IllegalArgumentException("An identifier is required to retrieve an instance of GeoRegion");        
        return entityManager().find(GeoRegion.class, id);        
    }    
    
    public static List<GeoRegion> GeoRegion.findGeoRegionEntries(int firstResult, int maxResults) {    
        return entityManager().createQuery("select o from GeoRegion o").setFirstResult(firstResult).setMaxResults(maxResults).getResultList();        
    }    
    
}

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
import org.ala.bie.rego.model.Infosource;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Infosource_Roo_Entity {
    
    @PersistenceContext    
    transient EntityManager Infosource.entityManager;    
    
    @Id    
    @GeneratedValue(strategy = GenerationType.AUTO)    
    @Column(name = "id")    
    private Long Infosource.id;    
    
    @Version    
    @Column(name = "version")    
    private Integer Infosource.version;    
    
    public Long Infosource.getId() {    
        return this.id;        
    }    
    
    public void Infosource.setId(Long id) {    
        this.id = id;        
    }    
    
    public Integer Infosource.getVersion() {    
        return this.version;        
    }    
    
    public void Infosource.setVersion(Integer version) {    
        this.version = version;        
    }    
    
    @Transactional    
    public void Infosource.persist() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        this.entityManager.persist(this);        
    }    
    
    @Transactional    
    public void Infosource.remove() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        if (this.entityManager.contains(this)) {        
            this.entityManager.remove(this);            
        } else {        
            Infosource attached = this.entityManager.find(Infosource.class, this.id);            
            this.entityManager.remove(attached);            
        }        
    }    
    
    @Transactional    
    public void Infosource.flush() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        this.entityManager.flush();        
    }    
    
    @Transactional    
    public void Infosource.merge() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        Infosource merged = this.entityManager.merge(this);        
        this.entityManager.flush();        
        this.id = merged.getId();        
    }    
    
    public static final EntityManager Infosource.entityManager() {    
        EntityManager em = new Infosource().entityManager;        
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");        
        return em;        
    }    
    
    public static long Infosource.countInfosources() {    
        return (Long) entityManager().createQuery("select count(o) from Infosource o").getSingleResult();        
    }    
    
    public static List<Infosource> Infosource.findAllInfosources() {    
        return entityManager().createQuery("select o from Infosource o").getResultList();        
    }    
    
    public static Infosource Infosource.findInfosource(Long id) {    
        if (id == null) throw new IllegalArgumentException("An identifier is required to retrieve an instance of Infosource");        
        return entityManager().find(Infosource.class, id);        
    }    
    
    public static List<Infosource> Infosource.findInfosourceEntries(int firstResult, int maxResults) {    
        return entityManager().createQuery("select o from Infosource o").setFirstResult(firstResult).setMaxResults(maxResults).getResultList();        
    }    
    
}

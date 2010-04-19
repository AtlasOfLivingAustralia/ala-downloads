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
import org.ala.bie.rego.model.ScientificName;
import org.springframework.transaction.annotation.Transactional;

privileged aspect ScientificName_Roo_Entity {
    
    @PersistenceContext    
    transient EntityManager ScientificName.entityManager;    
    
    @Id    
    @GeneratedValue(strategy = GenerationType.AUTO)    
    @Column(name = "id")    
    private Long ScientificName.id;    
    
    @Version    
    @Column(name = "version")    
    private Integer ScientificName.version;    
    
    public Long ScientificName.getId() {    
        return this.id;        
    }    
    
    public void ScientificName.setId(Long id) {    
        this.id = id;        
    }    
    
    public Integer ScientificName.getVersion() {    
        return this.version;        
    }    
    
    public void ScientificName.setVersion(Integer version) {    
        this.version = version;        
    }    
    
    @Transactional    
    public void ScientificName.persist() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        this.entityManager.persist(this);        
    }    
    
    @Transactional    
    public void ScientificName.remove() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        if (this.entityManager.contains(this)) {        
            this.entityManager.remove(this);            
        } else {        
            ScientificName attached = this.entityManager.find(ScientificName.class, this.id);            
            this.entityManager.remove(attached);            
        }        
    }    
    
    @Transactional    
    public void ScientificName.flush() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        this.entityManager.flush();        
    }    
    
    @Transactional    
    public void ScientificName.merge() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        ScientificName merged = this.entityManager.merge(this);        
        this.entityManager.flush();        
        this.id = merged.getId();        
    }    
    
    public static final EntityManager ScientificName.entityManager() {    
        EntityManager em = new ScientificName().entityManager;        
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");        
        return em;        
    }    
    
    public static long ScientificName.countScientificNames() {    
        return (Long) entityManager().createQuery("select count(o) from ScientificName o").getSingleResult();        
    }    
    
    public static List<ScientificName> ScientificName.findAllScientificNames() {    
        return entityManager().createQuery("select o from ScientificName o").getResultList();        
    }    
    
    public static ScientificName ScientificName.findScientificName(Long id) {    
        if (id == null) throw new IllegalArgumentException("An identifier is required to retrieve an instance of ScientificName");        
        return entityManager().find(ScientificName.class, id);        
    }    
    
    public static List<ScientificName> ScientificName.findScientificNameEntries(int firstResult, int maxResults) {    
        return entityManager().createQuery("select o from ScientificName o").setFirstResult(firstResult).setMaxResults(maxResults).getResultList();        
    }    
    
}

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
import org.ala.bie.rego.model.Contact;
import org.springframework.transaction.annotation.Transactional;

privileged aspect Contact_Roo_Entity {
    
    @PersistenceContext    
    transient EntityManager Contact.entityManager;    
    
    @Id    
    @GeneratedValue(strategy = GenerationType.AUTO)    
    @Column(name = "id")    
    private Long Contact.id;    
    
    @Version    
    @Column(name = "version")    
    private Integer Contact.version;    
    
    public Long Contact.getId() {    
        return this.id;        
    }    
    
    public void Contact.setId(Long id) {    
        this.id = id;        
    }    
    
    public Integer Contact.getVersion() {    
        return this.version;        
    }    
    
    public void Contact.setVersion(Integer version) {    
        this.version = version;        
    }    
    
    @Transactional    
    public void Contact.persist() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        this.entityManager.persist(this);        
    }    
    
    @Transactional    
    public void Contact.remove() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        if (this.entityManager.contains(this)) {        
            this.entityManager.remove(this);            
        } else {        
            Contact attached = this.entityManager.find(Contact.class, this.id);            
            this.entityManager.remove(attached);            
        }        
    }    
    
    @Transactional    
    public void Contact.flush() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        this.entityManager.flush();        
    }    
    
    @Transactional    
    public void Contact.merge() {    
        if (this.entityManager == null) this.entityManager = entityManager();        
        Contact merged = this.entityManager.merge(this);        
        this.entityManager.flush();        
        this.id = merged.getId();        
    }    
    
    public static final EntityManager Contact.entityManager() {    
        EntityManager em = new Contact().entityManager;        
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");        
        return em;        
    }    
    
    public static long Contact.countContacts() {    
        return (Long) entityManager().createQuery("select count(o) from Contact o").getSingleResult();        
    }    
    
    public static List<Contact> Contact.findAllContacts() {    
        return entityManager().createQuery("select o from Contact o").getResultList();        
    }    
    
    public static Contact Contact.findContact(Long id) {    
        if (id == null) throw new IllegalArgumentException("An identifier is required to retrieve an instance of Contact");        
        return entityManager().find(Contact.class, id);        
    }    
    
    public static List<Contact> Contact.findContactEntries(int firstResult, int maxResults) {    
        return entityManager().createQuery("select o from Contact o").setFirstResult(firstResult).setMaxResults(maxResults).getResultList();        
    }    
    
}

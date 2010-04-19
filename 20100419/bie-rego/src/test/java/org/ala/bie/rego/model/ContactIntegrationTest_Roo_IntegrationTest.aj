package org.ala.bie.rego.model;

import org.ala.bie.rego.model.ContactDataOnDemand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect ContactIntegrationTest_Roo_IntegrationTest {
    
    declare @type: ContactIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);    
    
    declare @type: ContactIntegrationTest: @ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml");    
    
    @Autowired    
    private ContactDataOnDemand ContactIntegrationTest.dod;    
    
    @Test    
    public void ContactIntegrationTest.testCountContacts() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Contact' failed to initialize correctly", dod.getRandomContact());        
        long count = org.ala.bie.rego.model.Contact.countContacts();        
        org.junit.Assert.assertTrue("Counter for 'Contact' incorrectly reported there were no entries", count > 0);        
    }    
    
    @Test    
    public void ContactIntegrationTest.testFindContact() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Contact' failed to initialize correctly", dod.getRandomContact());        
        java.lang.Long id = dod.getRandomContact().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'Contact' failed to provide an identifier", id);        
        org.ala.bie.rego.model.Contact obj = org.ala.bie.rego.model.Contact.findContact(id);        
        org.junit.Assert.assertNotNull("Find method for 'Contact' illegally returned null for id '" + id + "'", obj);        
        org.junit.Assert.assertEquals("Find method for 'Contact' returned the incorrect identifier", id, obj.getId());        
    }    
    
    @Test    
    public void ContactIntegrationTest.testFindAllContacts() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Contact' failed to initialize correctly", dod.getRandomContact());        
        long count = org.ala.bie.rego.model.Contact.countContacts();        
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'Contact', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);        
        java.util.List<org.ala.bie.rego.model.Contact> result = org.ala.bie.rego.model.Contact.findAllContacts();        
        org.junit.Assert.assertNotNull("Find all method for 'Contact' illegally returned null", result);        
        org.junit.Assert.assertTrue("Find all method for 'Contact' failed to return any data", result.size() > 0);        
    }    
    
    @Test    
    public void ContactIntegrationTest.testFindContactEntries() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Contact' failed to initialize correctly", dod.getRandomContact());        
        long count = org.ala.bie.rego.model.Contact.countContacts();        
        if (count > 20) count = 20;        
        java.util.List<org.ala.bie.rego.model.Contact> result = org.ala.bie.rego.model.Contact.findContactEntries(0, (int)count);        
        org.junit.Assert.assertNotNull("Find entries method for 'Contact' illegally returned null", result);        
        org.junit.Assert.assertEquals("Find entries method for 'Contact' returned an incorrect number of entries", count, result.size());        
    }    
    
    @Test    
    @Transactional    
    public void ContactIntegrationTest.testFlush() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Contact' failed to initialize correctly", dod.getRandomContact());        
        java.lang.Long id = dod.getRandomContact().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'Contact' failed to provide an identifier", id);        
        org.ala.bie.rego.model.Contact obj = org.ala.bie.rego.model.Contact.findContact(id);        
        org.junit.Assert.assertNotNull("Find method for 'Contact' illegally returned null for id '" + id + "'", obj);        
        boolean modified =  dod.modifyContact(obj);        
        java.lang.Integer currentVersion = obj.getVersion();        
        obj.flush();        
        org.junit.Assert.assertTrue("Version for 'Contact' failed to increment on flush directive", obj.getVersion() > currentVersion || !modified);        
    }    
    
    @Test    
    @Transactional    
    public void ContactIntegrationTest.testMerge() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Contact' failed to initialize correctly", dod.getRandomContact());        
        java.lang.Long id = dod.getRandomContact().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'Contact' failed to provide an identifier", id);        
        org.ala.bie.rego.model.Contact obj = org.ala.bie.rego.model.Contact.findContact(id);        
        org.junit.Assert.assertNotNull("Find method for 'Contact' illegally returned null for id '" + id + "'", obj);        
        boolean modified =  dod.modifyContact(obj);        
        java.lang.Integer currentVersion = obj.getVersion();        
        obj.merge();        
        obj.flush();        
        org.junit.Assert.assertTrue("Version for 'Contact' failed to increment on merge and flush directive", obj.getVersion() > currentVersion || !modified);        
    }    
    
    @Test    
    @Transactional    
    public void ContactIntegrationTest.testPersist() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Contact' failed to initialize correctly", dod.getRandomContact());        
        org.ala.bie.rego.model.Contact obj = dod.getNewTransientContact(Integer.MAX_VALUE);        
        org.junit.Assert.assertNotNull("Data on demand for 'Contact' failed to provide a new transient entity", obj);        
        org.junit.Assert.assertNull("Expected 'Contact' identifier to be null", obj.getId());        
        obj.persist();        
        obj.flush();        
        org.junit.Assert.assertNotNull("Expected 'Contact' identifier to no longer be null", obj.getId());        
    }    
    
    @Test    
    @Transactional    
    public void ContactIntegrationTest.testRemove() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Contact' failed to initialize correctly", dod.getRandomContact());        
        java.lang.Long id = dod.getRandomContact().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'Contact' failed to provide an identifier", id);        
        org.ala.bie.rego.model.Contact obj = org.ala.bie.rego.model.Contact.findContact(id);        
        org.junit.Assert.assertNotNull("Find method for 'Contact' illegally returned null for id '" + id + "'", obj);        
        obj.remove();        
        org.junit.Assert.assertNull("Failed to remove 'Contact' with identifier '" + id + "'", org.ala.bie.rego.model.Contact.findContact(id));        
    }    
    
}

package org.ala.bie.rego.model;

import org.ala.bie.rego.model.ScientificNameDataOnDemand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect ScientificNameIntegrationTest_Roo_IntegrationTest {
    
    declare @type: ScientificNameIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);    
    
    declare @type: ScientificNameIntegrationTest: @ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml");    
    
    @Autowired    
    private ScientificNameDataOnDemand ScientificNameIntegrationTest.dod;    
    
    @Test    
    public void ScientificNameIntegrationTest.testCountScientificNames() {    
        org.junit.Assert.assertNotNull("Data on demand for 'ScientificName' failed to initialize correctly", dod.getRandomScientificName());        
        long count = org.ala.bie.rego.model.ScientificName.countScientificNames();        
        org.junit.Assert.assertTrue("Counter for 'ScientificName' incorrectly reported there were no entries", count > 0);        
    }    
    
    @Test    
    public void ScientificNameIntegrationTest.testFindScientificName() {    
        org.junit.Assert.assertNotNull("Data on demand for 'ScientificName' failed to initialize correctly", dod.getRandomScientificName());        
        java.lang.Long id = dod.getRandomScientificName().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'ScientificName' failed to provide an identifier", id);        
        org.ala.bie.rego.model.ScientificName obj = org.ala.bie.rego.model.ScientificName.findScientificName(id);        
        org.junit.Assert.assertNotNull("Find method for 'ScientificName' illegally returned null for id '" + id + "'", obj);        
        org.junit.Assert.assertEquals("Find method for 'ScientificName' returned the incorrect identifier", id, obj.getId());        
    }    
    
    @Test    
    public void ScientificNameIntegrationTest.testFindAllScientificNames() {    
        org.junit.Assert.assertNotNull("Data on demand for 'ScientificName' failed to initialize correctly", dod.getRandomScientificName());        
        long count = org.ala.bie.rego.model.ScientificName.countScientificNames();        
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'ScientificName', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);        
        java.util.List<org.ala.bie.rego.model.ScientificName> result = org.ala.bie.rego.model.ScientificName.findAllScientificNames();        
        org.junit.Assert.assertNotNull("Find all method for 'ScientificName' illegally returned null", result);        
        org.junit.Assert.assertTrue("Find all method for 'ScientificName' failed to return any data", result.size() > 0);        
    }    
    
    @Test    
    public void ScientificNameIntegrationTest.testFindScientificNameEntries() {    
        org.junit.Assert.assertNotNull("Data on demand for 'ScientificName' failed to initialize correctly", dod.getRandomScientificName());        
        long count = org.ala.bie.rego.model.ScientificName.countScientificNames();        
        if (count > 20) count = 20;        
        java.util.List<org.ala.bie.rego.model.ScientificName> result = org.ala.bie.rego.model.ScientificName.findScientificNameEntries(0, (int)count);        
        org.junit.Assert.assertNotNull("Find entries method for 'ScientificName' illegally returned null", result);        
        org.junit.Assert.assertEquals("Find entries method for 'ScientificName' returned an incorrect number of entries", count, result.size());        
    }    
    
    @Test    
    @Transactional    
    public void ScientificNameIntegrationTest.testFlush() {    
        org.junit.Assert.assertNotNull("Data on demand for 'ScientificName' failed to initialize correctly", dod.getRandomScientificName());        
        java.lang.Long id = dod.getRandomScientificName().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'ScientificName' failed to provide an identifier", id);        
        org.ala.bie.rego.model.ScientificName obj = org.ala.bie.rego.model.ScientificName.findScientificName(id);        
        org.junit.Assert.assertNotNull("Find method for 'ScientificName' illegally returned null for id '" + id + "'", obj);        
        boolean modified =  dod.modifyScientificName(obj);        
        java.lang.Integer currentVersion = obj.getVersion();        
        obj.flush();        
        org.junit.Assert.assertTrue("Version for 'ScientificName' failed to increment on flush directive", obj.getVersion() > currentVersion || !modified);        
    }    
    
    @Test    
    @Transactional    
    public void ScientificNameIntegrationTest.testMerge() {    
        org.junit.Assert.assertNotNull("Data on demand for 'ScientificName' failed to initialize correctly", dod.getRandomScientificName());        
        java.lang.Long id = dod.getRandomScientificName().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'ScientificName' failed to provide an identifier", id);        
        org.ala.bie.rego.model.ScientificName obj = org.ala.bie.rego.model.ScientificName.findScientificName(id);        
        org.junit.Assert.assertNotNull("Find method for 'ScientificName' illegally returned null for id '" + id + "'", obj);        
        boolean modified =  dod.modifyScientificName(obj);        
        java.lang.Integer currentVersion = obj.getVersion();        
        obj.merge();        
        obj.flush();        
        org.junit.Assert.assertTrue("Version for 'ScientificName' failed to increment on merge and flush directive", obj.getVersion() > currentVersion || !modified);        
    }    
    
    @Test    
    @Transactional    
    public void ScientificNameIntegrationTest.testPersist() {    
        org.junit.Assert.assertNotNull("Data on demand for 'ScientificName' failed to initialize correctly", dod.getRandomScientificName());        
        org.ala.bie.rego.model.ScientificName obj = dod.getNewTransientScientificName(Integer.MAX_VALUE);        
        org.junit.Assert.assertNotNull("Data on demand for 'ScientificName' failed to provide a new transient entity", obj);        
        org.junit.Assert.assertNull("Expected 'ScientificName' identifier to be null", obj.getId());        
        obj.persist();        
        obj.flush();        
        org.junit.Assert.assertNotNull("Expected 'ScientificName' identifier to no longer be null", obj.getId());        
    }    
    
    @Test    
    @Transactional    
    public void ScientificNameIntegrationTest.testRemove() {    
        org.junit.Assert.assertNotNull("Data on demand for 'ScientificName' failed to initialize correctly", dod.getRandomScientificName());        
        java.lang.Long id = dod.getRandomScientificName().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'ScientificName' failed to provide an identifier", id);        
        org.ala.bie.rego.model.ScientificName obj = org.ala.bie.rego.model.ScientificName.findScientificName(id);        
        org.junit.Assert.assertNotNull("Find method for 'ScientificName' illegally returned null for id '" + id + "'", obj);        
        obj.remove();        
        org.junit.Assert.assertNull("Failed to remove 'ScientificName' with identifier '" + id + "'", org.ala.bie.rego.model.ScientificName.findScientificName(id));        
    }    
    
}

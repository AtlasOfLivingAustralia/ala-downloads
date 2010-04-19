package org.ala.bie.rego.model;

import org.ala.bie.rego.model.InfosourceDataOnDemand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect InfosourceIntegrationTest_Roo_IntegrationTest {
    
    declare @type: InfosourceIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);    
    
    declare @type: InfosourceIntegrationTest: @ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml");    
    
    @Autowired    
    private InfosourceDataOnDemand InfosourceIntegrationTest.dod;    
    
    @Test    
    public void InfosourceIntegrationTest.testCountInfosources() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Infosource' failed to initialize correctly", dod.getRandomInfosource());        
        long count = org.ala.bie.rego.model.Infosource.countInfosources();        
        org.junit.Assert.assertTrue("Counter for 'Infosource' incorrectly reported there were no entries", count > 0);        
    }    
    
    @Test    
    public void InfosourceIntegrationTest.testFindInfosource() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Infosource' failed to initialize correctly", dod.getRandomInfosource());        
        java.lang.Long id = dod.getRandomInfosource().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'Infosource' failed to provide an identifier", id);        
        org.ala.bie.rego.model.Infosource obj = org.ala.bie.rego.model.Infosource.findInfosource(id);        
        org.junit.Assert.assertNotNull("Find method for 'Infosource' illegally returned null for id '" + id + "'", obj);        
        org.junit.Assert.assertEquals("Find method for 'Infosource' returned the incorrect identifier", id, obj.getId());        
    }    
    
    @Test    
    public void InfosourceIntegrationTest.testFindAllInfosources() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Infosource' failed to initialize correctly", dod.getRandomInfosource());        
        long count = org.ala.bie.rego.model.Infosource.countInfosources();        
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'Infosource', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);        
        java.util.List<org.ala.bie.rego.model.Infosource> result = org.ala.bie.rego.model.Infosource.findAllInfosources();        
        org.junit.Assert.assertNotNull("Find all method for 'Infosource' illegally returned null", result);        
        org.junit.Assert.assertTrue("Find all method for 'Infosource' failed to return any data", result.size() > 0);        
    }    
    
    @Test    
    public void InfosourceIntegrationTest.testFindInfosourceEntries() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Infosource' failed to initialize correctly", dod.getRandomInfosource());        
        long count = org.ala.bie.rego.model.Infosource.countInfosources();        
        if (count > 20) count = 20;        
        java.util.List<org.ala.bie.rego.model.Infosource> result = org.ala.bie.rego.model.Infosource.findInfosourceEntries(0, (int)count);        
        org.junit.Assert.assertNotNull("Find entries method for 'Infosource' illegally returned null", result);        
        org.junit.Assert.assertEquals("Find entries method for 'Infosource' returned an incorrect number of entries", count, result.size());        
    }    
    
    @Test    
    @Transactional    
    public void InfosourceIntegrationTest.testFlush() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Infosource' failed to initialize correctly", dod.getRandomInfosource());        
        java.lang.Long id = dod.getRandomInfosource().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'Infosource' failed to provide an identifier", id);        
        org.ala.bie.rego.model.Infosource obj = org.ala.bie.rego.model.Infosource.findInfosource(id);        
        org.junit.Assert.assertNotNull("Find method for 'Infosource' illegally returned null for id '" + id + "'", obj);        
        boolean modified =  dod.modifyInfosource(obj);        
        java.lang.Integer currentVersion = obj.getVersion();        
        obj.flush();        
        org.junit.Assert.assertTrue("Version for 'Infosource' failed to increment on flush directive", obj.getVersion() > currentVersion || !modified);        
    }    
    
    @Test    
    @Transactional    
    public void InfosourceIntegrationTest.testMerge() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Infosource' failed to initialize correctly", dod.getRandomInfosource());        
        java.lang.Long id = dod.getRandomInfosource().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'Infosource' failed to provide an identifier", id);        
        org.ala.bie.rego.model.Infosource obj = org.ala.bie.rego.model.Infosource.findInfosource(id);        
        org.junit.Assert.assertNotNull("Find method for 'Infosource' illegally returned null for id '" + id + "'", obj);        
        boolean modified =  dod.modifyInfosource(obj);        
        java.lang.Integer currentVersion = obj.getVersion();        
        obj.merge();        
        obj.flush();        
        org.junit.Assert.assertTrue("Version for 'Infosource' failed to increment on merge and flush directive", obj.getVersion() > currentVersion || !modified);        
    }    
    
    @Test    
    @Transactional    
    public void InfosourceIntegrationTest.testPersist() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Infosource' failed to initialize correctly", dod.getRandomInfosource());        
        org.ala.bie.rego.model.Infosource obj = dod.getNewTransientInfosource(Integer.MAX_VALUE);        
        org.junit.Assert.assertNotNull("Data on demand for 'Infosource' failed to provide a new transient entity", obj);        
        org.junit.Assert.assertNull("Expected 'Infosource' identifier to be null", obj.getId());        
        obj.persist();        
        obj.flush();        
        org.junit.Assert.assertNotNull("Expected 'Infosource' identifier to no longer be null", obj.getId());        
    }    
    
    @Test    
    @Transactional    
    public void InfosourceIntegrationTest.testRemove() {    
        org.junit.Assert.assertNotNull("Data on demand for 'Infosource' failed to initialize correctly", dod.getRandomInfosource());        
        java.lang.Long id = dod.getRandomInfosource().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'Infosource' failed to provide an identifier", id);        
        org.ala.bie.rego.model.Infosource obj = org.ala.bie.rego.model.Infosource.findInfosource(id);        
        org.junit.Assert.assertNotNull("Find method for 'Infosource' illegally returned null for id '" + id + "'", obj);        
        obj.remove();        
        org.junit.Assert.assertNull("Failed to remove 'Infosource' with identifier '" + id + "'", org.ala.bie.rego.model.Infosource.findInfosource(id));        
    }    
    
}

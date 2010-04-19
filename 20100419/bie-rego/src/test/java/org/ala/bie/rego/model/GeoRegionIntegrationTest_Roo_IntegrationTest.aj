package org.ala.bie.rego.model;

import org.ala.bie.rego.model.GeoRegionDataOnDemand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect GeoRegionIntegrationTest_Roo_IntegrationTest {
    
    declare @type: GeoRegionIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);    
    
    declare @type: GeoRegionIntegrationTest: @ContextConfiguration(locations = "classpath:/META-INF/spring/applicationContext.xml");    
    
    @Autowired    
    private GeoRegionDataOnDemand GeoRegionIntegrationTest.dod;    
    
    @Test    
    public void GeoRegionIntegrationTest.testCountGeoRegions() {    
        org.junit.Assert.assertNotNull("Data on demand for 'GeoRegion' failed to initialize correctly", dod.getRandomGeoRegion());        
        long count = org.ala.bie.rego.model.GeoRegion.countGeoRegions();        
        org.junit.Assert.assertTrue("Counter for 'GeoRegion' incorrectly reported there were no entries", count > 0);        
    }    
    
    @Test    
    public void GeoRegionIntegrationTest.testFindGeoRegion() {    
        org.junit.Assert.assertNotNull("Data on demand for 'GeoRegion' failed to initialize correctly", dod.getRandomGeoRegion());        
        java.lang.Long id = dod.getRandomGeoRegion().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'GeoRegion' failed to provide an identifier", id);        
        org.ala.bie.rego.model.GeoRegion obj = org.ala.bie.rego.model.GeoRegion.findGeoRegion(id);        
        org.junit.Assert.assertNotNull("Find method for 'GeoRegion' illegally returned null for id '" + id + "'", obj);        
        org.junit.Assert.assertEquals("Find method for 'GeoRegion' returned the incorrect identifier", id, obj.getId());        
    }    
    
    @Test    
    public void GeoRegionIntegrationTest.testFindAllGeoRegions() {    
        org.junit.Assert.assertNotNull("Data on demand for 'GeoRegion' failed to initialize correctly", dod.getRandomGeoRegion());        
        long count = org.ala.bie.rego.model.GeoRegion.countGeoRegions();        
        org.junit.Assert.assertTrue("Too expensive to perform a find all test for 'GeoRegion', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);        
        java.util.List<org.ala.bie.rego.model.GeoRegion> result = org.ala.bie.rego.model.GeoRegion.findAllGeoRegions();        
        org.junit.Assert.assertNotNull("Find all method for 'GeoRegion' illegally returned null", result);        
        org.junit.Assert.assertTrue("Find all method for 'GeoRegion' failed to return any data", result.size() > 0);        
    }    
    
    @Test    
    public void GeoRegionIntegrationTest.testFindGeoRegionEntries() {    
        org.junit.Assert.assertNotNull("Data on demand for 'GeoRegion' failed to initialize correctly", dod.getRandomGeoRegion());        
        long count = org.ala.bie.rego.model.GeoRegion.countGeoRegions();        
        if (count > 20) count = 20;        
        java.util.List<org.ala.bie.rego.model.GeoRegion> result = org.ala.bie.rego.model.GeoRegion.findGeoRegionEntries(0, (int)count);        
        org.junit.Assert.assertNotNull("Find entries method for 'GeoRegion' illegally returned null", result);        
        org.junit.Assert.assertEquals("Find entries method for 'GeoRegion' returned an incorrect number of entries", count, result.size());        
    }    
    
    @Test    
    @Transactional    
    public void GeoRegionIntegrationTest.testFlush() {    
        org.junit.Assert.assertNotNull("Data on demand for 'GeoRegion' failed to initialize correctly", dod.getRandomGeoRegion());        
        java.lang.Long id = dod.getRandomGeoRegion().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'GeoRegion' failed to provide an identifier", id);        
        org.ala.bie.rego.model.GeoRegion obj = org.ala.bie.rego.model.GeoRegion.findGeoRegion(id);        
        org.junit.Assert.assertNotNull("Find method for 'GeoRegion' illegally returned null for id '" + id + "'", obj);        
        boolean modified =  dod.modifyGeoRegion(obj);        
        java.lang.Integer currentVersion = obj.getVersion();        
        obj.flush();        
        org.junit.Assert.assertTrue("Version for 'GeoRegion' failed to increment on flush directive", obj.getVersion() > currentVersion || !modified);        
    }    
    
    @Test    
    @Transactional    
    public void GeoRegionIntegrationTest.testMerge() {    
        org.junit.Assert.assertNotNull("Data on demand for 'GeoRegion' failed to initialize correctly", dod.getRandomGeoRegion());        
        java.lang.Long id = dod.getRandomGeoRegion().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'GeoRegion' failed to provide an identifier", id);        
        org.ala.bie.rego.model.GeoRegion obj = org.ala.bie.rego.model.GeoRegion.findGeoRegion(id);        
        org.junit.Assert.assertNotNull("Find method for 'GeoRegion' illegally returned null for id '" + id + "'", obj);        
        boolean modified =  dod.modifyGeoRegion(obj);        
        java.lang.Integer currentVersion = obj.getVersion();        
        obj.merge();        
        obj.flush();        
        org.junit.Assert.assertTrue("Version for 'GeoRegion' failed to increment on merge and flush directive", obj.getVersion() > currentVersion || !modified);        
    }    
    
    @Test    
    @Transactional    
    public void GeoRegionIntegrationTest.testPersist() {    
        org.junit.Assert.assertNotNull("Data on demand for 'GeoRegion' failed to initialize correctly", dod.getRandomGeoRegion());        
        org.ala.bie.rego.model.GeoRegion obj = dod.getNewTransientGeoRegion(Integer.MAX_VALUE);        
        org.junit.Assert.assertNotNull("Data on demand for 'GeoRegion' failed to provide a new transient entity", obj);        
        org.junit.Assert.assertNull("Expected 'GeoRegion' identifier to be null", obj.getId());        
        obj.persist();        
        obj.flush();        
        org.junit.Assert.assertNotNull("Expected 'GeoRegion' identifier to no longer be null", obj.getId());        
    }    
    
    @Test    
    @Transactional    
    public void GeoRegionIntegrationTest.testRemove() {    
        org.junit.Assert.assertNotNull("Data on demand for 'GeoRegion' failed to initialize correctly", dod.getRandomGeoRegion());        
        java.lang.Long id = dod.getRandomGeoRegion().getId();        
        org.junit.Assert.assertNotNull("Data on demand for 'GeoRegion' failed to provide an identifier", id);        
        org.ala.bie.rego.model.GeoRegion obj = org.ala.bie.rego.model.GeoRegion.findGeoRegion(id);        
        org.junit.Assert.assertNotNull("Find method for 'GeoRegion' illegally returned null for id '" + id + "'", obj);        
        obj.remove();        
        org.junit.Assert.assertNull("Failed to remove 'GeoRegion' with identifier '" + id + "'", org.ala.bie.rego.model.GeoRegion.findGeoRegion(id));        
    }    
    
}

package org.ala.bie.rego.web;

import java.lang.Long;
import java.lang.String;
import javax.validation.Valid;
import org.ala.bie.rego.model.GeoRegion;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

privileged aspect GeoRegionController_Roo_Controller {
    
    @RequestMapping(value = "/georegion", method = RequestMethod.POST)    
    public String GeoRegionController.create(@Valid GeoRegion geoRegion, BindingResult result, ModelMap modelMap) {    
        if (geoRegion == null) throw new IllegalArgumentException("A geoRegion is required");        
        if (result.hasErrors()) {        
            modelMap.addAttribute("geoRegion", geoRegion);            
            return "georegion/create";            
        }        
        geoRegion.persist();        
        return "redirect:/georegion/" + geoRegion.getId();        
    }    
    
    @RequestMapping(value = "/georegion/form", method = RequestMethod.GET)    
    public String GeoRegionController.createForm(ModelMap modelMap) {    
        modelMap.addAttribute("geoRegion", new GeoRegion());        
        return "georegion/create";        
    }    
    
    @RequestMapping(value = "/georegion/{id}", method = RequestMethod.GET)    
    public String GeoRegionController.show(@PathVariable("id") Long id, ModelMap modelMap) {    
        if (id == null) throw new IllegalArgumentException("An Identifier is required");        
        modelMap.addAttribute("geoRegion", GeoRegion.findGeoRegion(id));        
        return "georegion/show";        
    }    
    
    @RequestMapping(value = "/georegion", method = RequestMethod.GET)    
    public String GeoRegionController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, ModelMap modelMap) {    
        if (page != null || size != null) {        
            int sizeNo = size == null ? 10 : size.intValue();            
            modelMap.addAttribute("georegions", GeoRegion.findGeoRegionEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));            
            float nrOfPages = (float) GeoRegion.countGeoRegions() / sizeNo;            
            modelMap.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));            
        } else {        
            modelMap.addAttribute("georegions", GeoRegion.findAllGeoRegions());            
        }        
        return "georegion/list";        
    }    
    
    @RequestMapping(method = RequestMethod.PUT)    
    public String GeoRegionController.update(@Valid GeoRegion geoRegion, BindingResult result, ModelMap modelMap) {    
        if (geoRegion == null) throw new IllegalArgumentException("A geoRegion is required");        
        if (result.hasErrors()) {        
            modelMap.addAttribute("geoRegion", geoRegion);            
            return "georegion/update";            
        }        
        geoRegion.merge();        
        return "redirect:/georegion/" + geoRegion.getId();        
    }    
    
    @RequestMapping(value = "/georegion/{id}/form", method = RequestMethod.GET)    
    public String GeoRegionController.updateForm(@PathVariable("id") Long id, ModelMap modelMap) {    
        if (id == null) throw new IllegalArgumentException("An Identifier is required");        
        modelMap.addAttribute("geoRegion", GeoRegion.findGeoRegion(id));        
        return "georegion/update";        
    }    
    
    @RequestMapping(value = "/georegion/{id}", method = RequestMethod.DELETE)    
    public String GeoRegionController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {    
        if (id == null) throw new IllegalArgumentException("An Identifier is required");        
        GeoRegion.findGeoRegion(id).remove();        
        return "redirect:/georegion?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());        
    }    
    
}

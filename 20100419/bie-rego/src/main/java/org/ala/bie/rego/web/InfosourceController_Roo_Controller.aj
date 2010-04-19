package org.ala.bie.rego.web;

import java.lang.Long;
import java.lang.String;
import javax.validation.Valid;
import org.ala.bie.rego.model.GeoRegion;
import org.ala.bie.rego.model.Infosource;
import org.ala.bie.rego.model.ScientificName;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

privileged aspect InfosourceController_Roo_Controller {
    
    @RequestMapping(value = "/infosource", method = RequestMethod.POST)    
    public String InfosourceController.create(@Valid Infosource infosource, BindingResult result, ModelMap modelMap) {    
        if (infosource == null) throw new IllegalArgumentException("A infosource is required");        
        if (result.hasErrors()) {        
            modelMap.addAttribute("infosource", infosource);            
            modelMap.addAttribute("georegions", GeoRegion.findAllGeoRegions());            
            modelMap.addAttribute("scientificnames", ScientificName.findAllScientificNames());            
            modelMap.addAttribute("infosource_startDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));            
            modelMap.addAttribute("infosource_singleDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));            
            modelMap.addAttribute("infosource_endDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));            
            return "infosource/create";            
        }        
        infosource.persist();        
        return "redirect:/infosource/" + infosource.getId();        
    }    
    
    @RequestMapping(value = "/infosource/form", method = RequestMethod.GET)    
    public String InfosourceController.createForm(ModelMap modelMap) {    
        modelMap.addAttribute("infosource", new Infosource());        
        modelMap.addAttribute("georegions", GeoRegion.findAllGeoRegions());        
        modelMap.addAttribute("scientificnames", ScientificName.findAllScientificNames());        
        modelMap.addAttribute("infosource_startDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));        
        modelMap.addAttribute("infosource_singleDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));        
        modelMap.addAttribute("infosource_endDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));        
        return "infosource/create";        
    }    
    
    @RequestMapping(value = "/infosource/{id}", method = RequestMethod.GET)    
    public String InfosourceController.show(@PathVariable("id") Long id, ModelMap modelMap) {    
        if (id == null) throw new IllegalArgumentException("An Identifier is required");        
        modelMap.addAttribute("infosource_startDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));        
        modelMap.addAttribute("infosource_singleDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));        
        modelMap.addAttribute("infosource_endDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));        
        modelMap.addAttribute("infosource", Infosource.findInfosource(id));        
        return "infosource/show";        
    }    
    
    @RequestMapping(value = "/infosource", method = RequestMethod.GET)    
    public String InfosourceController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, ModelMap modelMap) {    
        if (page != null || size != null) {        
            int sizeNo = size == null ? 50 : size.intValue();            
            modelMap.addAttribute("infosources", Infosource.findInfosourceEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));            
            float nrOfPages = (float) Infosource.countInfosources() / sizeNo;            
            modelMap.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));            
        } else {        
            modelMap.addAttribute("infosources", Infosource.findAllInfosources());            
        }        
        modelMap.addAttribute("infosource_startDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));        
        modelMap.addAttribute("infosource_singleDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));        
        modelMap.addAttribute("infosource_endDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));        
        return "infosource/list";        
    }    
    
    @RequestMapping(method = RequestMethod.PUT)    
    public String InfosourceController.update(@Valid Infosource infosource, BindingResult result, ModelMap modelMap) {    
        if (infosource == null) throw new IllegalArgumentException("A infosource is required");        
        if (result.hasErrors()) {        
            modelMap.addAttribute("infosource", infosource);            
            modelMap.addAttribute("georegions", GeoRegion.findAllGeoRegions());            
            modelMap.addAttribute("scientificnames", ScientificName.findAllScientificNames());            
            modelMap.addAttribute("infosource_startDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));            
            modelMap.addAttribute("infosource_singleDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));            
            modelMap.addAttribute("infosource_endDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));            
            return "infosource/update";            
        }        
        infosource.merge();        
        return "redirect:/infosource/" + infosource.getId();        
    }    
    
    @RequestMapping(value = "/infosource/{id}/form", method = RequestMethod.GET)    
    public String InfosourceController.updateForm(@PathVariable("id") Long id, ModelMap modelMap) {    
        if (id == null) throw new IllegalArgumentException("An Identifier is required");        
        modelMap.addAttribute("infosource", Infosource.findInfosource(id));        
        modelMap.addAttribute("georegions", GeoRegion.findAllGeoRegions());        
        modelMap.addAttribute("scientificnames", ScientificName.findAllScientificNames());        
        modelMap.addAttribute("infosource_startDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));        
        modelMap.addAttribute("infosource_singleDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));        
        modelMap.addAttribute("infosource_endDate_date_format", org.joda.time.format.DateTimeFormat.patternForStyle("S-", org.springframework.context.i18n.LocaleContextHolder.getLocale()));        
        return "infosource/update";        
    }    
    
    @RequestMapping(value = "/infosource/{id}", method = RequestMethod.DELETE)    
    public String InfosourceController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {    
        if (id == null) throw new IllegalArgumentException("An Identifier is required");        
        Infosource.findInfosource(id).remove();        
        return "redirect:/infosource?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());        
    }    
    
}

package org.ala.bie.rego.web;

import java.lang.Long;
import java.lang.String;
import javax.validation.Valid;
import org.ala.bie.rego.model.ScientificName;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

privileged aspect ScientificNameController_Roo_Controller {
    
    @RequestMapping(value = "/scientificname", method = RequestMethod.POST)    
    public String ScientificNameController.create(@Valid ScientificName scientificName, BindingResult result, ModelMap modelMap) {    
        if (scientificName == null) throw new IllegalArgumentException("A scientificName is required");        
        if (result.hasErrors()) {        
            modelMap.addAttribute("scientificName", scientificName);            
            return "scientificname/create";            
        }        
        scientificName.persist();        
        return "redirect:/scientificname/" + scientificName.getId();        
    }    
    
    @RequestMapping(value = "/scientificname/form", method = RequestMethod.GET)    
    public String ScientificNameController.createForm(ModelMap modelMap) {    
        modelMap.addAttribute("scientificName", new ScientificName());        
        return "scientificname/create";        
    }    
    
    @RequestMapping(value = "/scientificname/{id}", method = RequestMethod.GET)    
    public String ScientificNameController.show(@PathVariable("id") Long id, ModelMap modelMap) {    
        if (id == null) throw new IllegalArgumentException("An Identifier is required");        
        modelMap.addAttribute("scientificName", ScientificName.findScientificName(id));        
        return "scientificname/show";        
    }    
    
    @RequestMapping(value = "/scientificname", method = RequestMethod.GET)    
    public String ScientificNameController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, ModelMap modelMap) {    
        if (page != null || size != null) {        
            int sizeNo = size == null ? 10 : size.intValue();            
            modelMap.addAttribute("scientificnames", ScientificName.findScientificNameEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));            
            float nrOfPages = (float) ScientificName.countScientificNames() / sizeNo;            
            modelMap.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));            
        } else {        
            modelMap.addAttribute("scientificnames", ScientificName.findAllScientificNames());            
        }        
        return "scientificname/list";        
    }    
    
    @RequestMapping(method = RequestMethod.PUT)    
    public String ScientificNameController.update(@Valid ScientificName scientificName, BindingResult result, ModelMap modelMap) {    
        if (scientificName == null) throw new IllegalArgumentException("A scientificName is required");        
        if (result.hasErrors()) {        
            modelMap.addAttribute("scientificName", scientificName);            
            return "scientificname/update";            
        }        
        scientificName.merge();        
        return "redirect:/scientificname/" + scientificName.getId();        
    }    
    
    @RequestMapping(value = "/scientificname/{id}/form", method = RequestMethod.GET)    
    public String ScientificNameController.updateForm(@PathVariable("id") Long id, ModelMap modelMap) {    
        if (id == null) throw new IllegalArgumentException("An Identifier is required");        
        modelMap.addAttribute("scientificName", ScientificName.findScientificName(id));        
        return "scientificname/update";        
    }    
    
    @RequestMapping(value = "/scientificname/{id}", method = RequestMethod.DELETE)    
    public String ScientificNameController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {    
        if (id == null) throw new IllegalArgumentException("An Identifier is required");        
        ScientificName.findScientificName(id).remove();        
        return "redirect:/scientificname?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());        
    }    
    
}

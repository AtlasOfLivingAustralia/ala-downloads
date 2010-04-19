package org.ala.bie.rego.web;

import java.lang.Long;
import java.lang.String;
import javax.validation.Valid;
import org.ala.bie.rego.model.Contact;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

privileged aspect ContactController_Roo_Controller {
    
    @RequestMapping(value = "/contact", method = RequestMethod.POST)    
    public String ContactController.create(@Valid Contact contact, BindingResult result, ModelMap modelMap) {    
        if (contact == null) throw new IllegalArgumentException("A contact is required");        
        if (result.hasErrors()) {        
            modelMap.addAttribute("contact", contact);            
            return "contact/create";            
        }        
        contact.persist();        
        return "redirect:/contact/" + contact.getId();        
    }    
    
    @RequestMapping(value = "/contact/form", method = RequestMethod.GET)    
    public String ContactController.createForm(ModelMap modelMap) {    
        modelMap.addAttribute("contact", new Contact());        
        return "contact/create";        
    }    
    
    @RequestMapping(value = "/contact/{id}", method = RequestMethod.GET)    
    public String ContactController.show(@PathVariable("id") Long id, ModelMap modelMap) {    
        if (id == null) throw new IllegalArgumentException("An Identifier is required");        
        modelMap.addAttribute("contact", Contact.findContact(id));        
        return "contact/show";        
    }    
    
    @RequestMapping(value = "/contact", method = RequestMethod.GET)    
    public String ContactController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, ModelMap modelMap) {    
        if (page != null || size != null) {        
            int sizeNo = size == null ? 10 : size.intValue();            
            modelMap.addAttribute("contacts", Contact.findContactEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));            
            float nrOfPages = (float) Contact.countContacts() / sizeNo;            
            modelMap.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));            
        } else {        
            modelMap.addAttribute("contacts", Contact.findAllContacts());            
        }        
        return "contact/list";        
    }    
    
    @RequestMapping(method = RequestMethod.PUT)    
    public String ContactController.update(@Valid Contact contact, BindingResult result, ModelMap modelMap) {    
        if (contact == null) throw new IllegalArgumentException("A contact is required");        
        if (result.hasErrors()) {        
            modelMap.addAttribute("contact", contact);            
            return "contact/update";            
        }        
        contact.merge();        
        return "redirect:/contact/" + contact.getId();        
    }    
    
    @RequestMapping(value = "/contact/{id}/form", method = RequestMethod.GET)    
    public String ContactController.updateForm(@PathVariable("id") Long id, ModelMap modelMap) {    
        if (id == null) throw new IllegalArgumentException("An Identifier is required");        
        modelMap.addAttribute("contact", Contact.findContact(id));        
        return "contact/update";        
    }    
    
    @RequestMapping(value = "/contact/{id}", method = RequestMethod.DELETE)    
    public String ContactController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {    
        if (id == null) throw new IllegalArgumentException("An Identifier is required");        
        Contact.findContact(id).remove();        
        return "redirect:/contact?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());        
    }    
    
}

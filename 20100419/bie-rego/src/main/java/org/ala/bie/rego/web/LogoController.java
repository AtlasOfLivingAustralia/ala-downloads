package org.ala.bie.rego.web;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.ala.bie.rego.model.Infosource;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/logo/**")
public class LogoController {
	
	@PersistenceUnit  
	private EntityManagerFactory emf; 
	private EntityManager em;  
	
	@PostConstruct  
	public void init() {  
		em = emf.createEntityManager();   
	}

	@RequestMapping("/logo/upload")
	public String uploadForm() {
		System.out.println("Reached...");
		return "logo/upload";
	}
	
	@RequestMapping("/logo/uploadLogo")
	public String uploadLogoForInfosource(@RequestParam("infosourceId") Integer infosourceId) {
		System.out.println("Reached...infosource id: "+infosourceId);
		return "logo/uploadLogo";
	}
	
	@RequestMapping(value = "/logo/storeLogo", method = RequestMethod.POST)
	@Transactional
	public String uploadComplete(@RequestParam("infosourceId") Long infosourceId, @RequestParam("file") MultipartFile file) {

	  if (!file.isEmpty()) {
		try{  
		    byte[] data = file.getBytes();
		    
		    System.out.println("Infosource received: "+infosourceId+", bytes received: "+data.length);
		    System.out.println("Content type: "+file.getContentType());
		    System.out.println("Original file: "+file.getOriginalFilename());
		    //write to temporary file ?
		    
		    File infoDir = new File("/data/bie/"+infosourceId);
		    if(!infoDir.exists()){
		    	FileUtils.forceMkdir(infoDir);
		    }
		    
		    File storedLogo = new File(infoDir.getAbsolutePath()+File.separator+file.getOriginalFilename());
		    FileUtils.writeByteArrayToFile(storedLogo, data);
		    //copy to /data/bie/infosource-id/<logo-filename>
		    
		    // store the bytes somewhere
		    
		    
		    //FIX ME - must be a better way to do this, without handling the transactino
		    //see http://forum.springsource.org/showthread.php?t=63896
		    Infosource infosource = em.find(Infosource.class, infosourceId);
		    infosource.setLogoUrl(file.getOriginalFilename());
		    em.getTransaction().begin();
		    try {
				em.persist(infosource);
				em.flush();
				em.getTransaction().commit();
		    }catch (Exception e){
		    	em.getTransaction().rollback();
		    	e.printStackTrace();
			}
//		    return "redirect:uploadSuccess";
		    return "redirect:/infosource/"+infosourceId;
		} catch(Exception e){
			e.printStackTrace();
			return "redirect:uploadFailure";
		}
	  } else {
	    return "redirect:uploadFailure";
	  }
	}	
	

	@RequestMapping(value = "/logo/uploaded", method = RequestMethod.POST)
	public String uploadComplete(@RequestParam("file") MultipartFile file) {

	  if (!file.isEmpty()) {
		try{  
		    byte[] bytes = file.getBytes();
		    System.out.println("Bytes received: "+bytes.length);

		    //write to temporary file ?
		    
		    //copy to /data/bie/infosource-id/<logo-filename>
		    
		    
		    // store the bytes somewhere
		    return "redirect:uploadSuccess";
		} catch(Exception e){
			e.printStackTrace();
			return "redirect:uploadFailure";
		}
	  } else {
	    return "redirect:uploadFailure";
	  }
	}
	
	@RequestMapping("/logo/uploadSuccess")
	public String uploadSuccess() {
		System.out.println("Reached...");
		return "logo/uploadSuccess";
	}	
	
	@RequestMapping("/logo/uploadFailure")
	public String uploadFailure() {
		System.out.println("Reached...");
		return "logo/uploadFailure";
	}

	/**
	 * @return the emf
	 */
	public EntityManagerFactory getEmf() {
		return emf;
	}

	/**
	 * @param emf the emf to set
	 */
	public void setEmf(EntityManagerFactory emf) {
		this.emf = emf;
	}

	/**
	 * @return the em
	 */
	public EntityManager getEm() {
		return em;
	}

	/**
	 * @param em the em to set
	 */
	public void setEm(EntityManager em) {
		this.em = em;
	}	
}
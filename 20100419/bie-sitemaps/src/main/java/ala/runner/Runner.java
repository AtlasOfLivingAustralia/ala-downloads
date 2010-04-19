/***************************************************************************
 * Copyright (C) 2009 Atlas of Living Australia
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ***************************************************************************/
package ala.runner;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ReflectionUtils;

/**
 * A runner class that will run all site map generators.
 *
 * @author Dave Martin (David.Martin@csiro.au)
 */
public class Runner {

	public static void main(String[] args) throws Exception {
		if(args.length>0){
			
			Set<BeanDefinition> bds = getSiteMapGenerators();
			Iterator<BeanDefinition> iter = bds.iterator();
			while(iter.hasNext()){
				BeanDefinition bd = iter.next();
				try{
					System.out.println("Running site map generator: "+bd.getBeanClassName());
					Class siteMapClass = Class.forName(bd.getBeanClassName());
					Object siteMapGenerator = siteMapClass.newInstance();
					Method mainMethod = siteMapClass.getMethod("main", String[].class);
					ReflectionUtils.invokeMethod(mainMethod, siteMapGenerator, new Object[]{new String[]{}});
				} catch(InstantiationException e) {
					System.err.println("Problem instantiating '"+bd.getBeanClassName()+"', Please check there is a default constructor.");
					e.printStackTrace();
				} catch(Exception e){
					System.err.println("Problem running class '"+bd.getBeanClassName()+"', Please check there is a default constructor, and main method.");
					e.printStackTrace();
				}
			}
		} else {
			listSiteMapGenerators();
		}
	}
	
	/**
	 * List the generators on the classpath
	 * 
	 * @param args
	 */
	public static Set<BeanDefinition> getSiteMapGenerators() {
		
		ClassPathScanningCandidateComponentProvider cpsccp 
			= new ClassPathScanningCandidateComponentProvider(false);
		
		cpsccp.addIncludeFilter(new TypeFilter(){
			@Override
			public boolean match(MetadataReader reader, MetadataReaderFactory readerFactory)
					throws IOException {
				AnnotationMetadata am = reader.getAnnotationMetadata();
				Set<String> types = am.getAnnotationTypes();
				for(String type: types){
					if(type.equalsIgnoreCase("ala.infosource.SiteMapGenerator")){
						return true;					
					}
				}
				return false;
			}
		});
		return cpsccp.findCandidateComponents("ala/infosource");
	}
	
	/**
	 * List the generators on the classpath with their titles.
	 * 
	 * @param args
	 */
	public static void listSiteMapGenerators() {
		
		System.out.println("=====================================");
		
		System.out.println("Site map generators detected in class path:\n");
		
		ClassPathScanningCandidateComponentProvider cpsccp 
			= new ClassPathScanningCandidateComponentProvider(false);
	
		cpsccp.addIncludeFilter(new TypeFilter(){
			@Override
			public boolean match(MetadataReader reader, MetadataReaderFactory readerFactory)
					throws IOException {
				ClassMetadata cm = reader.getClassMetadata();
				AnnotationMetadata am = reader.getAnnotationMetadata();
				Set<String> types = am.getAnnotationTypes();
				for(String type: types){
					if(type.equalsIgnoreCase("ala.infosource.SiteMapGenerator")){
						Map<String, Object> attr = am.getAnnotationAttributes("ala.infosource.SiteMapGenerator");
						System.out.println(cm.getClassName()+" - "+attr.get("longName"));
						return true;					
					}
				}
				return false;
			}
		});
		cpsccp.findCandidateComponents("ala/infosource");
		System.out.println("=====================================");
	}
}

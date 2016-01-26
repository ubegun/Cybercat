/**Copyright 2013 The Cybercat project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cybercat.automation;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.axis.utils.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.cybercat.automation.persistence.Criterion;
import org.cybercat.automation.persistence.model.Entity;
import org.cybercat.automation.persistence.model.PageModelException;
import org.cybercat.automation.utils.WorkFolder;


/**
 * Manages process of saving and accessing project data model
 *  - AutomationMain.getMainFactory().getPersistenceManager()
 */
public class PersistenceManager {

    
    private Marshaller createMarshaller(Class<?> clazz) throws AutomationFrameworkException{
        try {
            return JAXBContext.newInstance(clazz).createMarshaller();
        } catch (JAXBException e) {
            throw new AutomationFrameworkException(e);
        }
    }

    private Unmarshaller createUnmarshaller(Class<?> clazz) throws AutomationFrameworkException{
        try {
            return JAXBContext.newInstance(clazz).createUnmarshaller();
        } catch (JAXBException e) {
            throw new AutomationFrameworkException(e);
        }
    }    
    /**
     * Saves entity in work directory as xml file
     * 
     * @throws PageModelException
     */
    public <T extends Entity> void save(T entity) throws PageModelException {
        try {
            Marshaller marshaller = createMarshaller(entity.getClass());
            marshaller.setProperty("jaxb.formatted.output", new Boolean(true));
            marshaller.marshal(entity, createOutputFile(entity));
        } catch (Exception e) {
            throw new PageModelException(e);
        }
    }

    public String toJSONString(Object entity) throws PageModelException {
      try{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Marshaller marshaller = createMarshaller(entity.getClass());
        marshaller.setProperty("jaxb.formatted.output", new Boolean(true));
        marshaller.marshal(entity, out);      
        String ret = out.toString("UTF-8");
        return ret;
      } catch (Exception e){
        throw new PageModelException(e);
      }
      
    }
    
    /**
     * Loads and returns all objects of defined type
     * 
     * @param clazz
     *            - type of bins returned
     * @throws PageModelException
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity> List<T> load(Class<T> clazz) throws PageModelException {
        try {
            File[] files = createFileList(clazz);
            List<T> entries = new ArrayList<T>();
            for (int i = 0; i < files.length; i++) {
            	if (StringUtils.containsIgnoreCase(files[i].getAbsolutePath(), ".xml")){
            		entries.add((T) createUnmarshaller(clazz).unmarshal(files[i]));
            	}
            }
            return entries;
        } catch (Exception e) {
            throw new PageModelException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> T loadFirst(Class<T> clazz) throws PageModelException {
        try {
            File[] files = createFileList(clazz);
            if(files.length >0){
                return (T) createUnmarshaller(clazz).unmarshal(files[0]);
            }
            return null;
        } catch (Exception e) {
            throw new PageModelException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Entity> T findFirst(Class<T> clazz, Criterion<T> criterion) throws PageModelException {
        try {
            File[] files = createFileList(clazz);
            T entry;
            for (int i = 0; i < files.length; i++) {
                entry = (T) createUnmarshaller(clazz).unmarshal(files[i]);
                if (criterion.processRestriction(entry)) {
                    return entry;
                }
            }
            return null;
        } catch (Exception e) {
            throw new PageModelException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Entity> List<T> findAll(Class<T> clazz, Criterion<T> criterion) throws PageModelException {
        try {
            File[] files = createFileList(clazz);
            T entry;
            List<T> entries = new ArrayList<T>();
            for (int i = 0; i < files.length; i++) {
                entry = (T) createUnmarshaller(clazz).unmarshal(files[i]);
                if (criterion.processRestriction(entry)) {
                    entries.add(entry);
                }
            }
            return entries;
        } catch (Exception e) {
            throw new PageModelException(e);
        }
    }
    
    private static <T extends Entity> File[] createFileList(final Class<T> clazz) throws IOException {
        WorkFolder wf = WorkFolder.valueOf(clazz.getSimpleName());
        if(wf!= null){
            return wf.getPath().toFile().listFiles();            
        }else{
            wf =  WorkFolder.Model;
            return wf.getPath().toFile().listFiles(new FilenameFilter() {
                
                @Override
                public boolean accept(File dir, String name) {
                    return Pattern.matches(clazz.getSimpleName() + "\\d*\\.xml", name);
                }
            });   
        }        
    }

    private static <T extends Entity> File createOutputFile(T entry) throws IOException {
        WorkFolder wf = WorkFolder.valueOf(entry.getClass().getSimpleName());
        if(wf== null)
           wf =  WorkFolder.Model;
        Path outFile = Paths.get(wf.getPath().toString(), entry.getClass()
                .getSimpleName() + entry.getId() + ".xml");
        return outFile.toFile();
    }

}

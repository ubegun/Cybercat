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
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.cybercat.automation.persistence.Criterion;
import org.cybercat.automation.persistence.model.Entity;
import org.cybercat.automation.persistence.model.PageModelException;
import org.cybercat.automation.utils.WorkFolder;


/**
 * Manages process of saving and accessing project data model
 */
public class PersistenceManager {

    Marshaller marshller;
    Unmarshaller unmarshller;

    /**
     * Manages process of saving and accessing project data model
     * 
     * @param marshller
     * @param unmarshller
     */
    public PersistenceManager(Marshaller marshller, Unmarshaller unmarshller) {
        this.marshller = marshller;
        this.unmarshller = unmarshller;
    }

    /**
     * Saves entity in work directory as xml file
     * 
     * @throws PageModelException
     */
    public <T extends Entity> void save(T entity) throws PageModelException {
        try {
        	marshller.setProperty("jaxb.formatted.output", new Boolean(true));
            marshller.marshal(entity, createOutputFile(entity));
        } catch (Exception e) {
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
            File[] files = createinputSource(clazz).toFile().listFiles();
            List<T> entries = new ArrayList<T>();
            for (int i = 0; i < files.length; i++) {
            	if (StringUtils.containsIgnoreCase(files[i].getAbsolutePath(), ".xml")){
            		entries.add((T) unmarshller.unmarshal(files[i]));
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
            File[] files = createinputSource(clazz).toFile().listFiles();
            if(files.length >0){
                return (T) unmarshller.unmarshal(files[0]);
            }
            return null;
        } catch (Exception e) {
            throw new PageModelException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Entity> T findFirst(Class<T> clazz, Criterion<T> criterion) throws PageModelException {
        try {
            File[] files = createinputSource(clazz).toFile().listFiles();
            T entry;
            for (int i = 0; i < files.length; i++) {
                entry = (T) unmarshller.unmarshal(files[i]);
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
            File[] files = createinputSource(clazz).toFile().listFiles();
            T entry;
            List<T> entries = new ArrayList<T>();
            for (int i = 0; i < files.length; i++) {
                entry = (T) unmarshller.unmarshal(files[i]);
                if (criterion.processRestriction(entry)) {
                    entries.add(entry);
                }
            }
            return entries;
        } catch (Exception e) {
            throw new PageModelException(e);
        }
    }
    
    private static <T extends Entity> Path createinputSource(Class<T> clazz) throws IOException {
        return WorkFolder.valueOf(clazz.getSimpleName()).getPath();
    }

    private static <T extends Entity> File createOutputFile(T entry) throws IOException {
        Path outFile = Paths.get(WorkFolder.valueOf(entry.getClass().getSimpleName()).getPath().toString(), entry.getClass()
                .getSimpleName() + entry.getId() + ".xml");
        return outFile.toFile();
    }

}

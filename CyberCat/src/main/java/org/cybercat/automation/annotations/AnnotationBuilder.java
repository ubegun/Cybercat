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

package org.cybercat.automation.annotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Set;

import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageFactory;
import org.cybercat.automation.components.AbstractPageObject;
import org.cybercat.automation.core.AutomationMain;
import org.cybercat.automation.test.AbstractFeature;
import org.cybercat.automation.test.AbstractTestCase;
import org.cybercat.automation.test.IFeature;
import org.cybercat.automation.test.IVersionControl;
import org.reflections.Reflections;

/**
 * @author Ubegun
 *
 */
public class AnnotationBuilder {
    
    
    @SuppressWarnings("unchecked")
    public static final <T extends IFeature> void processCCPageObject(T entity) throws AutomationFrameworkException{
        AutomationMain mainFactory = AutomationMain.getMainFactory();
        PageFactory pageFactory =  mainFactory.getPageFactory();
        Field[] fields = entity.getClass().getDeclaredFields();
        for(int i= 0; i< fields.length; i++){
            fields[i].setAccessible(true);
            if(fields[i].getAnnotation(CCPageObject.class) != null){
                Class<AbstractPageObject> clazz;
                try{
                    clazz = (Class<AbstractPageObject>) fields[i].getType();
                }catch(Exception e){
                    throw new AutomationFrameworkException("Unexpected field type :" + fields[i].getType().getSimpleName()
                            + " field name: " + fields[i].getName()
                            + " class: " + entity.getClass().getSimpleName() + " Thread ID:" + Thread.currentThread().getId()
                            + " \n\tThis field must be of the type that extends AbstractPageObject class." , e); 
                }
                try {
                    AbstractPageObject po = pageFactory.createPage(clazz);
                    po.setPageFactory(pageFactory);
                    fields[i].set(entity, po);
                } catch (Exception e) {
                    throw new AutomationFrameworkException("Set filed exception. Please, save this log and contact the Cybercat project support." 
                            + " field name: " + fields[i].getName()
                            + " class: " + entity.getClass().getSimpleName() + " Thread ID:" + Thread.currentThread().getId()
                            ,e );
                }
            }
        }    
    }
    
    private static Reflections reflections;
    
    private static Reflections getReflections() throws AutomationFrameworkException{
        if(reflections != null)
            return reflections;
        AutomationMain mainFactory = AutomationMain.getMainFactory();
        String rootPackage = mainFactory.getProperty("version.control.root.package");        
        if(rootPackage == null )
            return null;
        reflections = new Reflections(rootPackage);
        return reflections;
    }
    
    
    @SuppressWarnings("unchecked")
    private final static <T extends IVersionControl> Class<T> versionControlPreprocessor(Class<T> providerzz) throws AutomationFrameworkException {
        AutomationMain mainFactory = AutomationMain.getMainFactory();
        Reflections refSearch;
        int version = 0;
        try{
            refSearch = getReflections();
            version = (int) mainFactory.getPropertyLong("app.version");
            if(refSearch == null || version < 0)
                return providerzz;
        }catch(Exception e ){    
            return providerzz;
        }        
        Set<Class<? extends T>> providers = refSearch.getSubTypesOf(providerzz);
        T candidate, caught = null;
        for (Class<? extends T> classProvider : providers) {
            try {
                Constructor<T> c = (Constructor<T>) classProvider.getConstructor();
                candidate = c.newInstance();
                if (candidate.getVersion() == version)
                    return (Class<T>) candidate.getClass();
                if (caught == null || (candidate.getVersion() < version && caught.getVersion() < candidate.getVersion())) {
                    caught = candidate;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (Class<T>) caught.getClass();
    }
    
    public static final <E extends AbstractTestCase> void processCCFeature(E entity) throws AutomationFrameworkException{
        processCCFeatureForObject(entity);
    }
    
    
    public static final <E extends IFeature> void processCCFeature(E entity) throws AutomationFrameworkException{
        processCCFeatureForObject(entity);
    }

    
    @SuppressWarnings("unchecked")
    private static final <T extends Object> void processCCFeatureForObject(T entity) throws AutomationFrameworkException{
        Field[] fields = entity.getClass().getDeclaredFields();
        for(int i= 0; i< fields.length; i++){
            fields[i].setAccessible(true);
            if(fields[i].getAnnotation(CCFeature.class) != null){
                Class<AbstractFeature> clazz;
                try{
                    clazz = (Class<AbstractFeature>) fields[i].getType();
                }catch(Exception e){
                    throw new AutomationFrameworkException("Unexpected field type :" + fields[i].getType().getSimpleName()
                            + " field name: " + fields[i].getName()
                            + " class: " + entity.getClass().getSimpleName() + " Thread ID:" + Thread.currentThread().getId()
                            + " \n\tThis field must be of the type that extends AbstractPageObject class." , e); 
                }
                try {
                    fields[i].set(entity, AbstractFeature.createFeature(versionControlPreprocessor(clazz)));
                } catch (Exception e) {
                    throw new AutomationFrameworkException("Set filed exception. Please, save this log and contact the Cybercat project support." 
                            + " field name: " + fields[i].getName()
                            + " class: " + entity.getClass().getSimpleName() + " Thread ID:" + Thread.currentThread().getId()
                            ,e );
                }                
            }
                
        }    
    }
    
    @SuppressWarnings("unchecked")
    public static final <T extends AbstractPageObject> void processCCPageFragment(T entity) throws AutomationFrameworkException{
        AutomationMain mainFactory = AutomationMain.getMainFactory();
        PageFactory pageFactory =  mainFactory.getPageFactory();
        Field[] fields = entity.getClass().getDeclaredFields();
        for(int i= 0; i< fields.length; i++){
            fields[i].setAccessible(true);
            if(fields[i].getAnnotation(CCPageFragment.class) != null){
                Class<AbstractPageObject> clazz;
                try{
                    clazz = (Class<AbstractPageObject>) fields[i].getType();
                }catch(Exception e){
                    throw new AutomationFrameworkException("Unexpected field type :" + fields[i].getType().getSimpleName()
                            + " field name: " + fields[i].getName()
                            + " class: " + entity.getClass().getSimpleName() + " Thread ID:" + Thread.currentThread().getId()
                            + " \n\tThis field must be of the type that extends AbstractPageObject class." , e); 
                }
                try {
                    AbstractPageObject fragment = pageFactory.createPage(clazz);
                    fragment.setPageFactory(pageFactory);
                    fields[i].set(entity, fragment);
                    entity.addPageFragment(fragment);
                } catch (Exception e) {
                    throw new AutomationFrameworkException("Set filed exception. Please, save this log and contact the Cybercat project support." 
                            + " field name: " + fields[i].getName()
                            + " class: " + entity.getClass().getSimpleName() + " Thread ID:" + Thread.currentThread().getId()
                            ,e );
                }
            }
        }
    }    

}

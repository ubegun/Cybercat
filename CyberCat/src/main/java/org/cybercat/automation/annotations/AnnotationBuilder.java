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

import org.apache.log4j.Logger;
import org.cybercat.automation.AutomationFrameworkException;
import org.cybercat.automation.PageFactory;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.components.AbstractPageObject;
import org.cybercat.automation.core.AutomationMain;
import org.cybercat.automation.core.TestStepAspect;
import org.cybercat.automation.core.integration.IIntegrationService;
import org.cybercat.automation.core.integration.IntegrationServiceAspect;
import org.cybercat.automation.test.AbstractFeature;
import org.cybercat.automation.test.AbstractTestCase;
import org.cybercat.automation.test.IFeature;
import org.cybercat.automation.test.IVersionControl;
import org.reflections.Reflections;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

/**
 * @author Ubegun
 *
 */
public class AnnotationBuilder {
    
    private static Logger log = Logger.getLogger(AnnotationBuilder.class);
    
    public static final <T extends IFeature> void processCCPageObject(T entity, Class<? extends Object> clazz) throws AutomationFrameworkException{
        if (clazz == null)
            return;
        for(Field field: clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if(field.getAnnotation(CCPageObject.class) != null){
                createPageObjectField(entity, field);
            }else if (field.getAnnotation(CCProperty.class) != null) {
                processPropertyField(entity, field);
            }
        }    
        processCCPageObject(entity, (Class<? extends Object>) clazz.getSuperclass());
    }
        
    public static final <T extends IFeature> void processCCPageObject(T entity) throws AutomationFrameworkException{
        processCCPageObject(entity, entity.getClass());
    }
    
    private static Reflections reflections;
    
    private static Reflections getReflections() throws AutomationFrameworkException{
        if(reflections != null)
            return reflections;
        String rootPackage = AutomationMain.getProperty("version.control.root.package");        
        if(rootPackage == null )
            return null;
        reflections = new Reflections(rootPackage);
        return reflections;
    }
    
    
    @SuppressWarnings("unchecked")
    private final static <T extends IVersionControl> Class<T> versionControlPreprocessor(Class<T> providerzz) throws AutomationFrameworkException {
        Reflections refSearch;
        int version = 0;
        Platform platform;
        try{
            refSearch = getReflections();
            version = (int) AutomationMain.getPropertyLong("app.version");
            platform = Platform.fromValue(AutomationMain.getProperty("platform.type"));
            if(refSearch == null || version < 0)
                return providerzz;
        }catch(Exception e ){    
            return providerzz;
        }        
        Set<Class<? extends T>> providers = refSearch.getSubTypesOf(providerzz);
        if(providers == null || providers.size() == 0)
            return  providerzz;
        T candidate, caught = null;
        for (Class<? extends T> classProvider : providers) {
            try {
                Constructor<T> c = (Constructor<T>) classProvider.getConstructor();
                if (!Modifier.isAbstract(classProvider.getModifiers())) {
                    candidate = c.newInstance();
                    if (candidate.getVersion() == version && candidate.isSupportsPlatform(platform))
                        return (Class<T>) candidate.getClass();
                    if ((caught == null || (candidate.getVersion() < version && caught.getVersion() < candidate.getVersion())) && candidate.isSupportsPlatform(platform)) {
                        caught = candidate;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (Class<T>) caught.getClass();
    }
    
    public static final <T extends AbstractTestCase> void processCCFeature(T entity) throws AutomationFrameworkException{
        processCCFeatureForObject(entity);
    }
    
    
    public static final <T extends IFeature> void processCCFeature(T entity) throws AutomationFrameworkException{
        processCCFeatureForObject(entity);
    }

    
    private static <T extends Object> void processCCFeatureForObject(T entity) throws AutomationFrameworkException {
        processCCFeatureForObject(entity, entity.getClass());
    }
    
    private static <T extends Object> void processCCFeatureForObject(T entity, Class<? extends Object> clazz) throws AutomationFrameworkException {
        if (clazz == null)
            return;
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getAnnotation(CCFeature.class) != null) {
                processCCFeatureField(entity, field);
            } else if (field.getAnnotation(CCIntegrationService.class) != null) {
                createIntegrationService(field, entity);
            } else if (field.getAnnotation(CCProperty.class) != null) {
                processPropertyField(entity, field);
            }
        }
        processCCFeatureForObject(entity, (Class<? extends Object>) clazz.getSuperclass());
    }

    
    public static final <T extends AbstractPageObject> void processCCPageFragment(T entity) throws AutomationFrameworkException {
        processCCPageFragment(entity, entity.getClass());
    }

    @SuppressWarnings("unchecked")
    private static final <T extends AbstractPageObject> void processCCPageFragment(T entity, Class<? extends AbstractPageObject> clazz) throws AutomationFrameworkException {
        if (clazz == null)
            return;
        Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            if (fields[i].getAnnotation(CCPageFragment.class) != null) {
                entity.addPageFragment(createPageObjectField(entity, fields[i]));
            }
        }
        processCCPageFragment(entity, (Class<? extends AbstractPageObject>) clazz.getSuperclass());
    }
    
    /**
     * @param targetObject
     * @param fields
     * @return
     * @throws AutomationFrameworkException
     * @throws PageObjectException
     */
    @SuppressWarnings("unchecked")
    private static <T extends AbstractPageObject> T createPageObjectField(Object targetObject, Field field) throws AutomationFrameworkException, PageObjectException {
        AutomationMain mainFactory = AutomationMain.getMainFactory();
        PageFactory pageFactory =  mainFactory.getPageFactory();
        Class<AbstractPageObject> clazz;
        try{
            clazz = (Class<AbstractPageObject>) field.getType();
        }catch(Exception e){
            throw new AutomationFrameworkException("Unexpected field type :" + field.getType().getSimpleName()
                    + " field name: " + field.getName()
                    + " class: " + targetObject.getClass().getSimpleName() + " Thread ID:" + Thread.currentThread().getId()
                    + " \n\tThis field must be of the type that extends AbstractPageObject class." , e); 
        }
        try {
            T po = (T) pageFactory.createPage(clazz);
            po.setPageFactory(pageFactory);
            field.set(targetObject, po);
            return po;
        } catch (Exception e) {
            throw new AutomationFrameworkException("Set filed exception. Please, save this log and contact the Cybercat project support." 
                    + " field name: " + field.getName()
                    + " class: " + targetObject.getClass().getSimpleName() + " Thread ID:" + Thread.currentThread().getId()
                    ,e );
        }
    }    

    
    /**
     * @param entity
     * @param fields
     * @param i
     * @return
     * @throws AutomationFrameworkException
     */
    @SuppressWarnings("unchecked")
    private static <T> Class<AbstractFeature> processCCFeatureField(T entity, Field field)
            throws AutomationFrameworkException {
        Class<AbstractFeature> clazz;
        try{
            clazz = (Class<AbstractFeature>) field.getType();
        }catch(Exception e){
            throw new AutomationFrameworkException("Unexpected field type :" + field.getType().getSimpleName()
                    + " field name: " + field.getName()
                    + " class: " + entity.getClass().getSimpleName() + " Thread ID:" + Thread.currentThread().getId()
                    + " \n\tThis field must be of the type that extends AbstractPageObject class." , e); 
        }
        try {
            field.set(entity, createFeature(versionControlPreprocessor(clazz)));
        } catch (Exception e) {
            throw new AutomationFrameworkException("Set filed exception. Please, save this log and contact the Cybercat project support." 
                    + " field name: " + field.getName()
                    + " class: " + entity.getClass().getSimpleName() + " Thread ID:" + Thread.currentThread().getId()
                    ,e );
        }
        return clazz;
    }    
    
    @SuppressWarnings("unchecked")
    private final static <T extends IFeature> T createFeature(Class<T> eType) throws AutomationFrameworkException {
        try {
            Constructor<T> cons = eType.getDeclaredConstructor();
            T result = cons.newInstance();
            AnnotationBuilder.processCCFeature(result);
            AnnotationBuilder.processCCPageObject(result);
            AspectJProxyFactory proxyFactory = new AspectJProxyFactory(result);
            proxyFactory.addAspect(TestStepAspect.class);
            result = (T) proxyFactory.getProxy();
            log.info(eType.getSimpleName() + " feature has been created.");
            return result;
        } catch (Exception e) {
            // handle test fail
            throw new AutomationFrameworkException("Feature factoring exception. ", e);            
        }
    }
    
    @SuppressWarnings("unchecked")
    private static final void createIntegrationService(Field field, Object targetObject) throws AutomationFrameworkException{  
        Class<IIntegrationService> clazz;
        try{
            clazz = (Class<IIntegrationService>) field.getType();                    
        }catch(Exception e){
            throw new AutomationFrameworkException("Unexpected field type :" + field.getType().getSimpleName()
                    + " field name: " + field.getName()
                    + " class: " + targetObject.getClass().getSimpleName() + " Thread ID:" + Thread.currentThread().getId()
                    + " \n\tThis field must be of the type that extends AbstractPageObject class." , e); 
        }
        try {
            
            field.set(targetObject, createIntegrationService(clazz, field.getAnnotation(CCIntegrationService.class)));
        } catch (Exception e) {
            throw new AutomationFrameworkException("Set filed exception. Please, save this log and contact the Cybercat project support." 
                    + " field name: " + field.getName()
                    + " class: " + targetObject.getClass().getSimpleName() + " Thread ID:" + Thread.currentThread().getId()
                    ,e );
        }                                

    }
    
    private static void processIntegrationService(Object targetObject, Class<? extends Object> clazz) throws AutomationFrameworkException{
        if (clazz == null)
            return;
        for(Field field : clazz.getDeclaredFields()){
            field.setAccessible(true);
            if(field.getAnnotation(CCProperty.class) != null){
                processPropertyField(targetObject, field);                                
            }
        }
        processIntegrationService(targetObject, clazz.getSuperclass());
    }

    /**
     * @param targetObject
     * @param fields
     * @param i
     * @throws AutomationFrameworkException
     */
    public static void processPropertyField(Object targetObject, Field field) throws AutomationFrameworkException {
        try {
            CCProperty properties = field.getAnnotation(CCProperty.class);
            StringBuffer value = new StringBuffer("");
            for(String prop :properties.value()){ 
                value.append(AutomationMain.getProperty(prop));
            }
            field.set(targetObject, value.toString());
        } catch (Exception e) {
            throw new AutomationFrameworkException(
                    "Set filed exception. Please, save this log and contact the Cybercat project support."
                            + " field name: " + field.getName() + " class: " + targetObject.getClass().getSimpleName()
                            + " Thread ID:" + Thread.currentThread().getId(), e);
        }
    }
    
    
    @SuppressWarnings("unchecked")
    private static final <T extends IIntegrationService> T createIntegrationService(Class<T> clazz, CCIntegrationService aService) throws AutomationFrameworkException{
        Class<T> cService =  versionControlPreprocessor(clazz);
        Constructor<T> cons;
        try {
            cons = cService.getConstructor();
            T result = cons.newInstance();
            processIntegrationService(result, result.getClass() );
            result.setup();
            AspectJProxyFactory proxyFactory = new AspectJProxyFactory(result);
            proxyFactory.addAspect(new IntegrationServiceAspect(aService.hasSession()));
            result = (T) proxyFactory.getProxy();
            log.info(cService.getSimpleName() + " integration Service has been created.");
            return result;            
        } catch (Exception e) {
            throw new AutomationFrameworkException("Integration service creation problem.", e);
        }
    }

}

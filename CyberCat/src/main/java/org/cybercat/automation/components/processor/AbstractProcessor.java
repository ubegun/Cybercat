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
package org.cybercat.automation.components.processor;


import java.util.Stack;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import org.apache.log4j.Logger;
import org.cybercat.automation.PageObjectException;
import org.cybercat.automation.browsers.Browser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class AbstractProcessor {
    
    private final static ForkJoinPool mainPool = new ForkJoinPool(100);

    private static Logger LOG = Logger.getLogger(AbstractProcessor.class);

    private WebDriverWait wait;

    public long implicitTimeout = 30; //default value
    public long explicitTimeout = 30; //default value
    private Stack<ForkPathFinder> tasks = new Stack<ForkPathFinder>(); 

    public AbstractProcessor(long implicitTimeout, long explicitTimeout){
        this.implicitTimeout = implicitTimeout;
        this.explicitTimeout = explicitTimeout;
    }
    
    /**
     * Returns located WebElement
     * @return 
     */
    @SuppressWarnings("rawtypes")
    public void initWebElementByCriteria(final Browser browser, AbstractCriteria criteria) {
        wait = browser.createWaitDriver(explicitTimeout);
        ForkPathFinder task = new ForkPathFinder(this, browser, criteria, 0,  implicitTimeout);
        try{ 
            mainPool.invoke(task);
        }catch(CancellationException e){
            //do nothing
        }
    }
    
    public abstract By getByElement(String path);

    public WebDriverWait getWait() {
        return this.wait;
    }
    
    public void setImplicitTimeout(long implicitTimeout) {
    	LOG.warn("Set implicit wait to: "+implicitTimeout);
        this.implicitTimeout = implicitTimeout;
    }
    
    public void setExplicitTimeout(long explicitTimeout) {
        this.explicitTimeout = explicitTimeout;
    }

    public long getImplicitTimeout() {
        return implicitTimeout;
    }
    public long getExplicitTimeout() {
        return explicitTimeout;
    }

    
    protected synchronized final void pushTask(ForkPathFinder task){
        tasks.push(task);
    }

    protected synchronized final void done(){
        while(!tasks.empty()){
            tasks.pop().cancel(true);
        }
    }        
    /**
     * This class contains criteria for iteration by paths array
     */
    /**
     * This class contains criteria for iteration by paths array
     */
    public static abstract class AbstractCriteria<T> {

        private String[] paths;
        private WebElement webElement;
        private boolean isFound = false;

        public AbstractCriteria(String[] paths) {            
            this.paths = paths;
        }
        
        public abstract  ExpectedCondition<T> getExpectedCondition(String path);

        public String[] getPaths() {
            return paths;
        }
        
        public WebElement getWebElement() {
            return webElement;
        }

        public void setWebElement(WebElement webElement) {
            this.webElement = webElement;
        }

        /**
         * Returns false in case the iteration by paths array should be ended
         */
        public abstract boolean onSuccess(T element, String path);
   
        
        /**
         * This method is called when the initialization process of the current element completes unsuccessfully
         * 
         * @throws PageObjectException
         */
        public void onException( String path) {
            LOG.warn("The element not found by path =>" + path);
        }
        
        /**
         * This method is called when the initialization process of the current element completes unsuccessfully
         * 
         * @throws PageObjectException
         */
        public void onException(Exception e, String path) {
            LOG.warn("The element not found by path =>" + path + "\t" + e.getMessage());
        }

        public boolean isFound() {
            return isFound;
        }

        public void setFound(boolean isFound) {
            this.isFound = isFound;
        }
    }
    
    @SuppressWarnings("serial")
    public static class ForkPathFinder extends RecursiveAction {

        @SuppressWarnings("rawtypes")
        private AbstractCriteria criteria;
        private int index;
        private Browser browser;
        private long implicitTimeout;
        private AbstractProcessor abstractProcessor;

        @SuppressWarnings("rawtypes")
        public ForkPathFinder(AbstractProcessor abstractProcessor, Browser browser, AbstractCriteria criteria, int index, long implicitTimeout) {
            this.abstractProcessor = abstractProcessor; 
            this.browser = browser;
            this.criteria = criteria;
            this.index = index; 
            this.implicitTimeout = implicitTimeout;
            abstractProcessor.pushTask(this);
        }

        @SuppressWarnings("unchecked")
        private void computeDirectly(){
            String path = criteria.getPaths()[index];
            WebDriverWait wait = browser.createWaitDriver(abstractProcessor.getExplicitTimeout());
            try {        
                LOG.debug("processing ---> " + path);
                criteria.setFound(criteria.onSuccess(wait.until(criteria.getExpectedCondition(path)), path));
                if (criteria.isFound()) {
                    LOG.debug("found!!!  ---> " + path);
                    abstractProcessor.done();
                }else{
                    criteria.onException(path);
                }
            } catch (Exception e) {
                if(criteria.isFound())
                    criteria.onException(path);  
                else 
                    criteria.onException(e, path);
            } finally{
                criteria = null;
                abstractProcessor = null;
                browser = null;
            }

        }

        @Override
        protected void compute() {
            if(criteria.getPaths().length > index + 1){
                ForkPathFinder fp = new ForkPathFinder(abstractProcessor, browser, criteria, index + 1, implicitTimeout);
                fp.fork();
            }
            computeDirectly();
        }
    }
}

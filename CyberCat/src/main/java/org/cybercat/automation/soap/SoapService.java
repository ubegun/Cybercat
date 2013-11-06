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
package org.cybercat.automation.soap;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.cybercat.automation.persistence.model.Identity;

public abstract class SoapService {

    private static Logger LOG = Logger.getLogger(SoapService.class);

    private ISessionManager soapSession;
    private boolean isAuthorized = false;

    
    public SoapService() {
        super();
    }

    public void createNewSession(Identity user) throws SoapServiceException {
        if (user == null) {
            soapSession = new MockSoapSession();
            LOG.warn(this.toString() + ": This SOAP service works without server authentication.");
            return;
        }
        soapSession = new SessionManager(user);
        doAuthorize();
    }

    public Identity getSessionOwner() {
        return soapSession == null ? null : soapSession.getCurrentUser();
    }

    private void doAuthorize() throws SoapServiceException {
        isAuthorized = authorize();
        soapSession.makeCookieSnapshot();
    }

    public ISessionManager getSoapSession() {
        return soapSession;
    }
    
    public boolean isAuthorized() {
        return isAuthorized;
    }

    public abstract boolean authorize() throws SoapServiceException;
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void outputDebugInfo(Object objectToBeBound){
        try {
            Envelope env = new Envelope(objectToBeBound);
            JAXBContext contect = JAXBContext.newInstance(env.getClass(), objectToBeBound.getClass());
            Marshaller marshaller = contect.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", new Boolean(true));
            System.out.println();  
            marshaller.marshal(env, System.out);
        } catch (Exception e) {
            LOG.error(e);
        }
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void outputDebugInfoFile(Object objectToBeBound, String filePath){
        try {
            Envelope env = new Envelope(objectToBeBound);
            JAXBContext contect = JAXBContext.newInstance(env.getClass(), objectToBeBound.getClass());
            Marshaller marshaller = contect.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", new Boolean(true));
            System.out.println();  
            
            File file = new File(filePath); 
            marshaller.marshal(env, file);
            
        } catch (Exception e) {
            LOG.error(e);
        }
    }
    
    @XmlRootElement(name = "envelope")
    public static class Envelope<T> {
        
        public Envelope(){ 
            super();
        }
        
        Envelope(T request){
            this.request = request;
        }
        
        private T request;

        public T getRequest() {
            return request;
        }

        public void setRequest(T request) {
            this.request = request;
        }
        
        
        
    }

    
}

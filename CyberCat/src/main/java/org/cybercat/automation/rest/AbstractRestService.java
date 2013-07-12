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
package org.cybercat.automation.rest;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.cybercat.automation.persistence.model.User;
import org.json.JSONObject;

public abstract class AbstractRestService {

    private static final Logger LOG = Logger.getLogger(AbstractRestService.class);

    protected User sessionOwner; 
    
    public AbstractRestService() throws RestServiceException{
        super();
    }

    public void createNewSession(User sessionOwner) throws RestServiceException {
        this.sessionOwner = sessionOwner;
        doAuthorize();
    }
    
    abstract void doAuthorize() throws RestServiceException;

    @Deprecated
    protected static JSONObject transform(Response responce) throws RestServiceException {
        String message = responce.readEntity(String.class);
        LOG.debug("REST > responce >  " + message);
        try {
            return new JSONObject(message);
        } catch (Exception e) {
            LOG.error("REST > responce >  JSONObject exception", e);
            throw new RestServiceException(e);
        }
    }

}

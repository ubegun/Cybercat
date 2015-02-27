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
package org.cybercat.automation.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cybercat.automation.persistence.ActionType;

public abstract class DataWorker {

    private final static Logger LOG = LogManager.getLogger(DataWorker.class);

    private Collection<DataWorker> workers;
    protected Collection<ActionType> supportedTypes;
    protected Object[] args;

    public DataWorker() {
        supportedTypes = new ArrayList<ActionType>();
        workers = new ArrayList<DataWorker>();
    }

    public abstract boolean verifyMethodParameters(Method method, Object[] args);

    public Object[] getData(ActionType type, Method method, Object[] args) {
        DataWorker thisWorker = getWorker(type);
        if (thisWorker.verifyMethodParameters(method, args)) {
            return thisWorker.getData(type, method, args);
        }
        LOG.error("The method parameters are unacceptable.");
        return args;
    }

    public boolean isSupported(ActionType type) {
        return supportedTypes.contains(type);
    }

    protected DataWorker getWorker(ActionType type) {
        for (DataWorker worker : workers) {
            if (worker.isSupported(type))
                return worker;
        }
        LOG.error("Unsupported action.");
        return null;
    }

    public void setWorkers(Collection<DataWorker> workers) {
        for (DataWorker worker : workers) {
            supportedTypes.addAll(worker.supportedTypes);
        }
        this.workers = workers;
    }

}

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

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;

public class WeakReferenceThreadScope implements Scope {

    private static final Logger logger = Logger.getLogger(WeakReferenceThreadScope.class);

    private final ThreadLocal<Map <String, WeakReference<Object>>> threadScope = new NamedThreadLocal<Map <String, WeakReference<Object>>>(
            "SimpleThreadScope") {
        @Override
        protected Map<String, WeakReference<Object>> initialValue() {
            return new HashMap<String, WeakReference<Object>>();
        }
    };

    @SuppressWarnings("rawtypes")
    public Object get(String name, ObjectFactory objectFactory) {
        System.gc();
        Map<String, WeakReference<Object>> scope = threadScope.get();
        WeakReference<Object> wRef = scope.get(name);
        Object object;        
        if (wRef == null || wRef.get() == null) {
            scope.remove(name);
            object = objectFactory.getObject();
            wRef = new WeakReference<Object>(object);
            scope.put(name, wRef);
            threadScope.set(scope);
        } else {
            object = wRef.get();
        }
        return object;
    }

    public Object remove(String name) {
        Map<String, WeakReference<Object>> scope = threadScope.get();
        WeakReference<Object> wRef = scope.remove(name);
        if (wRef == null || wRef.get() == null) {
            return null;
        }
        return wRef.get();
    }

    public void registerDestructionCallback(String name, Runnable callback) {
       //TODO: handle dectructor
        logger.warn("SimpleThreadScope does not support descruction callbacks. Consider using a RequestScope in a Web environment.");
    }

    public Object resolveContextualObject(String key) {
        return null;
    }

    public String getConversationId() {
        return Thread.currentThread().getName();
    }

}

package org.cybercat.automation.addons;

import java.lang.reflect.Field;

/**
 * Singleton addon factory for injection custom logic to Cybercat business entity like Page objects, Features and
 * Integration services. It will be used for factoring of fields, which are implements AddonService interface.
 *  Requirement for addon service implementation: 
 *    - public constructor without parameters. 
 *    - in case the factory cannot process incoming type of service, it will return null as result. 
 *    - relation between service and it factory must be like one to one or one factory can produce 
 *      many kind of service, but never many services produce one type of service.
 * 
 * @author ubegun
 */
public interface AddonServiceFactory<T extends AddonService> {

  T processAddonService(Field field);

}

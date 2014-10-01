/**
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package ch.ledcom.tomcat.valves.allocation;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * Created by gehel on 3/27/14.
 */
public final class Jmx {

    private final MBeanServer mBeanServer;

    public Jmx(MBeanServer mBeanServer) {
        this.mBeanServer = mBeanServer;
    }

    public static ObjectName newObjectName(String name) {
        try {
            return new ObjectName(name);
        } catch (MalformedObjectNameException e) {
            throw new JmxException("error creating ObjectName with name: " + name, e);
        }
    }

    public <T> T invoke(ObjectName objectName, String operation, Object[] arguments, String[] argumentsTypes, Class<T> resultType) {
        try {
            return (T) mBeanServer.invoke(objectName, operation, arguments, argumentsTypes);
        } catch (Exception e) {
            throw new JmxException("error invoking operation: " + operation + " on object: " + objectName);
        }
    }

    public boolean isRegistered(ObjectName objectName) {
        return mBeanServer.isRegistered(objectName);
    }

    public boolean getBooleanAttribute(ObjectName objectName, String attribute) {
        try {
            return Boolean.TRUE.equals(mBeanServer.getAttribute(objectName, attribute));
        } catch (Exception e) {
            return false;
        }
    }
}

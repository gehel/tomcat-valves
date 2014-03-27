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
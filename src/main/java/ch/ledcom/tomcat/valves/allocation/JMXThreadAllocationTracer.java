package ch.ledcom.tomcat.valves.allocation;

import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Created by gehel on 3/27/14.
 */
public class JMXThreadAllocationTracer implements ThreadAllocationTracer {

    private static final ObjectName THREADING_MBEAN = Jmx.newObjectName("java.lang:type=Threading");

    private final ThreadLocal<Long> allocationSize = new ThreadLocal<Long>();

    private final Jmx jmx = new Jmx(ManagementFactory.getPlatformMBeanServer());

    @Override
    public void mark() {
        allocationSize.set(retrieveCurrentThreadAllocation());
    }

    @Override
    public long allocatedSinceMark() {
        return retrieveCurrentThreadAllocation() - allocationSize.get();
    }

    private Long retrieveCurrentThreadAllocation() {
        long threadId = Thread.currentThread().getId();
        return jmx.invoke(THREADING_MBEAN, "getThreadAllocatedBytes", new Object[]{threadId}, new String[]{"long"}, Long.class);
    }

}

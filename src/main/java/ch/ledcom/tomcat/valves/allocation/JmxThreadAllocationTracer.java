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

import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

/**
 * Created by gehel on 3/27/14.
 */
public class JmxThreadAllocationTracer implements ThreadAllocationTracer {

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

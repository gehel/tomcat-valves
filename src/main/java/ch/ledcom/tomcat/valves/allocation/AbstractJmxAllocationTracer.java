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
 * Created by gehel on 28/03/14.
 */
public abstract class AbstractJmxAllocationTracer implements ThreadAllocationTracer {
    private static final ObjectName THREADING_MBEAN = Jmx.newObjectName("java.lang:type=Threading");

    private static final ThreadLocal<Jmx> jmx = new ThreadLocal<Jmx>(){
        @Override
        public Jmx initialValue() {
            return new Jmx(ManagementFactory.getPlatformMBeanServer());
        }
    };

    protected Long retrieveCurrentThreadAllocation() {
        long threadId = Thread.currentThread().getId();
        return jmx.get().invoke(
                THREADING_MBEAN,
                "getThreadAllocatedBytes",
                new Object[]{threadId},
                new String[]{"long"},
                Long.class);
    }

}

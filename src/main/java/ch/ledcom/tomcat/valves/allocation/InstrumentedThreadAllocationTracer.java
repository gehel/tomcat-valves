/**
 * Copyright (C) 2013 LedCom (guillaume.lederrey@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.ledcom.tomcat.valves.allocation;

import com.google.monitoring.runtime.instrumentation.AllocationRecorder;
import com.google.monitoring.runtime.instrumentation.Sampler;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 * Created by gehel on 3/27/14.
 */
public class InstrumentedThreadAllocationTracer implements ThreadAllocationTracer {

    private final ThreadLocal<Long> allocationSize = new AllocationSizeThreadLocal();

    public InstrumentedThreadAllocationTracer() {
        AllocationRecorder.addSampler(new ThreadAllocationSampler(allocationSize));

    }

    @Override
    public void mark() {
        allocationSize.set(0L);
    }

    @Override
    public long allocatedSinceMark() {
        return firstNonNull(allocationSize.get(), 0L);
    }

    private static final class ThreadAllocationSampler implements Sampler {

        private final ThreadLocal<Long> allocationSize;

        private ThreadAllocationSampler(ThreadLocal<Long> allocationSize) {
            this.allocationSize = allocationSize;
        }

        @Override
        public void sampleAllocation(int count, String desc, Object newObject, long size) {
            allocationSize.set(allocationSize.get() + size);
        }
    }

    private static final class AllocationSizeThreadLocal extends ThreadLocal<Long> {
        @Override
        public Long initialValue() {
            return 0L;
        }
    }
}

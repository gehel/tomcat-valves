package ch.ledcom.tomcat.valves.allocation;

import com.google.monitoring.runtime.instrumentation.AllocationRecorder;
import com.google.monitoring.runtime.instrumentation.Sampler;

/**
 * Created by gehel on 3/27/14.
 */
public class InstrumentedThreadAllocationTracer implements ThreadAllocationTracer {

    private final ThreadLocal<Long> allocationSize = new ThreadLocal<Long>(){
        @Override
        public Long initialValue() {
            return Long.valueOf(0L);
        }
    };

    public InstrumentedThreadAllocationTracer() {
        AllocationRecorder.addSampler(new Sampler() {
            @Override
            public void sampleAllocation(int count, String desc, Object newObject, long size) {
                allocationSize.set(allocationSize.get() + size);
            }
        });

    }

    @Override
    public void mark() {
        allocationSize.set(Long.valueOf(0L));
    }

    @Override
    public long allocatedSinceMark() {
        Long allocatedSinceMark = allocationSize.get();
        return allocatedSinceMark != null ? allocatedSinceMark : 0L;
    }
}

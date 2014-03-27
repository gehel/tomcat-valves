package ch.ledcom.tomcat.valves.allocation;

/**
 * Created by gehel on 27/03/14.
 */
final class DummyAllocator implements Runnable {
    private final int numberOfAllocations;
    private final ThreadAllocationTracer tracer;
    long allocated = 0;

    public DummyAllocator(int numberOfAllocations) {
        this(numberOfAllocations, null);
    }

    DummyAllocator(int numberOfAllocations, ThreadAllocationTracer tracer) {
        this.numberOfAllocations = numberOfAllocations;
        this.tracer = tracer;
    }

    @Override
    public void run() {
        if (tracer != null) {
            tracer.mark();
        }
        for (int i = 0; i < numberOfAllocations; i++) {
            new Integer(i);
        }
        if (tracer != null) {
            allocated = tracer.allocatedSinceMark();
        }
    }
}

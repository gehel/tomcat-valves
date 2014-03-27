package ch.ledcom.tomcat.valves.allocation;

import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static java.lang.String.format;
/**
 * Created by gehel on 27/03/14.
 */
public class JMXThreadAllocationTracerTest {

    ThreadAllocationTracer tracer;

    @Before
    public void initializeTracer() {
        tracer = new JMXThreadAllocationTracer();
    }

    @Test
    public void allocationIsRecorded() throws InterruptedException {
        tracer.mark();
        long allocatedAtStart = tracer.allocatedSinceMark();
        // allocate one int
        new Integer(0);
        long allocatedAtEnd = tracer.allocatedSinceMark();
        System.out.println(format("Allocated at start [%s]", allocatedAtStart));
        System.out.println(format("Allocated at end [%s]", allocatedAtEnd));
        assertThat(allocatedAtEnd, is(greaterThan(allocatedAtStart)));
    }

    @Test
    public void twoDifferentThreadsAllocateDifferentAmounts() throws InterruptedException {
        DummyAllocator allocator1 = new DummyAllocator(10, tracer);
        DummyAllocator allocator2 = new DummyAllocator(100, tracer);
        Thread t1 = new Thread(allocator1);
        Thread t2 = new Thread(allocator2);
        t1.start();
        t2.start();

        t1.join();
        t2.join();

        assertThat(allocator1.allocated, is(lessThan(allocator2.allocated)));
    }

}

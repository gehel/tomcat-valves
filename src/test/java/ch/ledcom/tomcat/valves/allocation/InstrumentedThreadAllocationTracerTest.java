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

import org.junit.Before;
import org.junit.Test;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

/**
 * Created by gehel on 27/03/14.
 */
public class InstrumentedThreadAllocationTracerTest {

    ThreadAllocationTracer tracer;

    @Before
    public void initializeTracer() {
        tracer = new InstrumentedThreadAllocationTracer();
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

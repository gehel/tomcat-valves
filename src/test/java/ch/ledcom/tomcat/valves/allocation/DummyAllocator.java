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

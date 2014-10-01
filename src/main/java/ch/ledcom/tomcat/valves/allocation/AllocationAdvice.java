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

import org.aspectj.lang.ProceedingJoinPoint;

import java.util.Set;

/**
 * This is meant to be used as a Spring AOP advice, it should be moved to a different project as it has nothing to do
 * with a Tomcat Valve, but it will come ...
 * Created by gehel on 27/03/14.
 */
public class AllocationAdvice {

    private final AllocationTracerFactory tracerFactory;
    private final Set<AllocationReporter> reporters;

    public AllocationAdvice(AllocationTracerFactory tracerFactory, Set<AllocationReporter> reporters) {
        this.tracerFactory = tracerFactory;
        this.reporters = reporters;
    }

    public Object traceAllocation(ProceedingJoinPoint pjp) throws Throwable {
        ThreadAllocationTracer tracer = tracerFactory.create();
        tracer.mark();
        Object retVal = pjp.proceed();
        long allocatedMemory = tracer.allocatedSinceMark();
        for (AllocationReporter reporter : reporters) {
            reporter.report(pjp.toShortString(), allocatedMemory);
        }
        return retVal;
    }
}

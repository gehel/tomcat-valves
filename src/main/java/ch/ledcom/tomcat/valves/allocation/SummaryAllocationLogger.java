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

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gehel on 3/27/14.
 */
public class SummaryAllocationLogger implements AllocationReporter {
    private static Log log = LogFactory.getLog(SummaryAllocationLogger.class);

    private final ConcurrentMap<String, AllocationCounter> perRequestTypeAllocation = new ConcurrentHashMap<String, AllocationCounter>();
    private final AtomicLong nbInvocations = new AtomicLong(0L);

    private final int printSummaryPeriod;

    public SummaryAllocationLogger(int printSummaryPeriod) {
        this.printSummaryPeriod = printSummaryPeriod;
    }

    @Override
    public void report(String context, Long totalRequestAllocation) {
        computeSummary(context, totalRequestAllocation);
        printSummary();
    }

    private void computeSummary(String requestURI, Long totalRequestAllocation) {
        if (!perRequestTypeAllocation.containsKey(requestURI)) {
            perRequestTypeAllocation.putIfAbsent(requestURI, new AllocationCounter());
        }
        AllocationCounter counter = perRequestTypeAllocation.get(requestURI);
        counter.calls.incrementAndGet();
        counter.totalAllocation.addAndGet(totalRequestAllocation);
    }

    private void printSummary() {
        if (nbInvocations.incrementAndGet() % printSummaryPeriod != 0) {
            return;
        }
        log.info("Summary of all requests:");
        log.info("<requestURI>:<calls>:<totalAllocation>");
        for (Map.Entry<String, AllocationCounter> entry : perRequestTypeAllocation.entrySet()) {
            log.info(entry.getKey() + ":" + entry.getValue().calls + ":" + entry.getValue().totalAllocation);
        }
    }

    private static final class AllocationCounter {
        private final AtomicLong calls = new AtomicLong(0L);
        private final AtomicLong totalAllocation = new AtomicLong(0L);
    }
}

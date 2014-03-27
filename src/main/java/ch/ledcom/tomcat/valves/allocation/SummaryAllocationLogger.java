package ch.ledcom.tomcat.valves.allocation;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gehel on 3/27/14.
 */
public class SummaryAllocationLogger implements AllocationReporter {
    private static Log log = LogFactory.getLog(SummaryAllocationLogger.class);

    private final ConcurrentHashMap<String, AllocationCounter> perRequestTypeAllocation = new ConcurrentHashMap<String, AllocationCounter>();
    private final AtomicLong nbInvocations = new AtomicLong(0L);

    private final int printSummaryPeriod;

    public SummaryAllocationLogger(int printSummaryPeriod) {
        this.printSummaryPeriod = printSummaryPeriod;
    }

    @Override
    public void report(String requestURI, Long totalRequestAllocation) {
        computeSummary(requestURI, totalRequestAllocation);
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

package ch.ledcom.tomcat.valves.allocation;

import ch.ledcom.tomcat.valves.SessionSerializableCheckerValve;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/**
 * Created by gehel on 3/27/14.
 */
public class AllocationLogger implements AllocationReporter {
    private static Log log = LogFactory.getLog(AllocationLogger.class);

    @Override
    public void report(String requestURI, Long totalRequestAllocation) {
        log.info("Memory allocated : " + requestURI + " - " + totalRequestAllocation);
    }
}

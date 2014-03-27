package ch.ledcom.tomcat.valves.allocation;

/**
 * Created by gehel on 3/27/14.
 */
public interface AllocationReporter {
    void report(String requestURI, Long totalRequestAllocation);
}

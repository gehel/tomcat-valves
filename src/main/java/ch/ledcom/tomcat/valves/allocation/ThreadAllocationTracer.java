package ch.ledcom.tomcat.valves.allocation;

/**
 * Created by gehel on 3/27/14.
 */
public interface ThreadAllocationTracer {
    void mark();
    long allocatedSinceMark();
}

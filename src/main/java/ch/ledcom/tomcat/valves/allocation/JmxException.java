package ch.ledcom.tomcat.valves.allocation;

/**
 * Created by gehel on 3/27/14.
 */
public class JmxException extends RuntimeException {
    public JmxException(String message) {
        super(message);
    }

    public JmxException(String message, Throwable cause) {
        super(message, cause);
    }
}

package ch.ledcom.tomcat.valves;

import com.google.monitoring.runtime.instrumentation.AllocationRecorder;
import com.google.monitoring.runtime.instrumentation.Sampler;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gehel on 26/03/14.
 */
public class RequestAllocationRecorder extends ValveBase {

    /** logger. */
    private static Log log = LogFactory
            .getLog(SessionSerializableCheckerValve.class);

    private final ThreadLocal<AtomicLong> allocationSize =
        new ThreadLocal<AtomicLong>() {
            @Override protected AtomicLong initialValue() {
                return new AtomicLong();
            }
    };

    public void RequestAllocationRecorder() {
        AllocationRecorder.addSampler(new Sampler() {
            @Override
            public void sampleAllocation(int count, String desc, Object newObject, long size) {
                allocationSize.get().addAndGet(size);
            }
        });
    }

    /**
     * Record the size allocated by the request.
     *
     * @param request
     *            the request being served
     * @param response
     *            the response being generated
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        // reset allocated size
        allocationSize.get().set(0);
        getNext().invoke(request, response);
        log.info(request.getRequestURI() + " - " + allocationSize.get().get());
    }
}

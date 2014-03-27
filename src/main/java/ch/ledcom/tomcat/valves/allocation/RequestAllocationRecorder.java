/**
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package ch.ledcom.tomcat.valves.allocation;

import ch.ledcom.tomcat.valves.SessionSerializableCheckerValve;
import com.google.common.collect.ImmutableSet;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gehel on 26/03/14.
 */
public class RequestAllocationRecorder extends ValveBase {

    public static final String PROP_PREFIX = RequestAllocationRecorder.class.getCanonicalName();

    public static final String PROP_DISABLED = PROP_PREFIX + ".disabled";
    public static final String PROP_PRINT_SUMMARY = PROP_PREFIX + ".printSummary";
    public static final String PROP_PRINT_SUMMARY_PERIOD = PROP_PREFIX + ".printSummary.period";

    private final boolean disabled;

    private final ThreadAllocationTracer threadAllocationTracer;

    private final ImmutableSet<AllocationReporter> reporters;

    public RequestAllocationRecorder() {
        disabled = parseBoolean(System.getProperty(PROP_DISABLED), false);
        boolean printSummary = parseBoolean(System.getProperty(PROP_PRINT_SUMMARY), false);
        int printSummaryPeriod = parseInt(System.getProperty(PROP_PRINT_SUMMARY_PERIOD), 1);

        if (!disabled) {
            threadAllocationTracer = new InstrumentedThreadAllocationTracer();
        } else {
            threadAllocationTracer = null;
        }

        ImmutableSet.Builder<AllocationReporter> builder = ImmutableSet.<AllocationReporter>builder();
        builder.add(new AllocationLogger());

        if (printSummary) {
            builder.add(new SummaryAllocationLogger(printSummaryPeriod));
        }
        reporters = builder.build();
    }

    /**
     * Record the size allocated by the request.
     *
     * @param request  the request being served
     * @param response the response being generated
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void invoke(final Request request, final Response response) throws IOException, ServletException {
        if (disabled) {
            getNext().invoke(request, response);
        } else {
            threadAllocationTracer.mark();
            getNext().invoke(request, response);

            String requestURI = request.getRequestURI();
            Long totalRequestAllocation = threadAllocationTracer.allocatedSinceMark();

            for(AllocationReporter reporter : reporters) {
                reporter.report(requestURI, totalRequestAllocation);
            }

        }
    }


    /**
     * Utility method to parse boolean.
     *
     * @param s            String to parse as a boolean
     * @param defaultValue default value to return in case the String is empty
     * @return the boolean represented by the String or the default value if
     * the String is empty
     */
    private static boolean parseBoolean(final String s, final boolean defaultValue) {
        if (s == null || s.trim().equals("")) {
            return defaultValue;
        }
        return Boolean.parseBoolean(s);
    }

    private int parseInt(final String s, final int defaultValue) {
        if (s == null || s.trim().equals("")) {
            return defaultValue;
        }
        return Integer.parseInt(s);
    }


}

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
package ch.ledcom.tomcat.valves;

import static java.lang.String.format;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import ch.ledcom.tomcat.valves.objectexplorer.MemoryMeasurer;

/**
 * Logs the size of the HTTP session.
 *
 * For this valve to work, a Java Agent must be registered to initialize the
 * object-explorer introspection. See README.md for details.
 *
 * @author gehel
 */
public class SessionSizeValve extends ValveBase {

    /** logger. */
    private static Log log = LogFactory.getLog(SessionSizeValve.class);

    /**
     * Logs the size of the HTTP session.
     *
     * @param request the request being served
     * @param response the response being generated
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public final void invoke(final Request request, final Response response)
            throws IOException, ServletException {
        try {
            getNext().invoke(request, response);
        } finally {
            if (!(request.getSession(false) == null)) {
                @SuppressWarnings("unchecked")
                Enumeration<String> attibuteNames = request.getSession()
                        .getAttributeNames();
                int sessionSize = 0;
                while (attibuteNames.hasMoreElements()) {
                    String attributeName = attibuteNames.nextElement();
                    sessionSize += MemoryMeasurer.measureBytes(request
                            .getSession().getAttribute(attributeName));
                }
                log.info(format("Session size = %d.", sessionSize));
            }
        }
    }
}

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

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import static java.lang.String.format;

public class SessionSerializableCheckerValve extends ValveBase {

    private static Log log = LogFactory
            .getLog(SessionSerializableCheckerValve.class);

    @Override
    public void invoke(Request request, Response response) throws IOException,
            ServletException {
        try {
            getNext().invoke(request, response);
        } finally {
            if (request.getSession(false) == null) {
                return;
            }
            @SuppressWarnings("unchecked")
            Enumeration<String> attibuteNames = request.getSession()
                    .getAttributeNames();
            while (attibuteNames.hasMoreElements()) {
                String attributeName = attibuteNames.nextElement();
                checkSerializable(request.getSession().getAttribute(
                        attributeName));
            }
        }
    }

    private void checkSerializable(Object attribute) {
        if (!Serializable.class.isAssignableFrom(attribute.getClass())) {
            log.warn(format(
                    "Session attribute [%s] of class [%s] is not serializeable.",
                    attribute, attribute.getClass()));
        }
    }
}

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
package ch.ledcom.tomcat.valves;

import static java.lang.String.format;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Enumeration;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closer;

/**
 * Checks if all session attributes are serializable.
 *
 * @author gehel
 */
public class SessionSerializableCheckerValve extends ValveBase {

    /** logger. */
    private static Log log = LogFactory
            .getLog(SessionSerializableCheckerValve.class);

    /**
     * Check if all session attributes are serializable.
     *
     * @param request
     *            the request being served
     * @param response
     *            the response being generated
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public final void invoke(final Request request, final Response response)
            throws IOException, ServletException {
        try {
            getNext().invoke(request, response);
        } finally {
            if (request.getSession(false) != null) {
                @SuppressWarnings("unchecked")
                final Enumeration<String> attibuteNames = request.getSession()
                        .getAttributeNames();
                while (attibuteNames.hasMoreElements()) {
                    final String attributeName = attibuteNames.nextElement();
                    final Object attribute = request.getSession().getAttribute(
                            attributeName);
                    try {
                        checkSerializable(attribute);
                    } catch (final Exception e) {
                        log.warn(
                                format("Session attribute [%s] of class [%s] threw "
                                        + "exception while serializing.",
                                        attribute, attribute.getClass()), e);
                    }
                }
            }
        }
    }

    /**
     * Check if an object is serializable, emit a warning log if it is not.
     *
     * @param attribute
     *            the attribute to check
     * @throws IOException
     */
    private void checkSerializable(final Object attribute) throws IOException {
        if (!Serializable.class.isAssignableFrom(attribute.getClass())) {
            log.warn(format("Session attribute [%s] of class [%s] is not "
                    + "serializable.", attribute, attribute.getClass()));
        }
        final Closer closer = Closer.create();
        try {
            final ObjectOutputStream out = closer.register(new ObjectOutputStream(
                    ByteStreams.nullOutputStream()));
            out.writeObject(attribute);
        } catch (Throwable t) {
            closer.rethrow(t);
        } finally {
            closer.close();
        }
    }
}

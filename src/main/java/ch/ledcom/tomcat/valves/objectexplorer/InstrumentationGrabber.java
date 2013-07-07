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
package ch.ledcom.tomcat.valves.objectexplorer;

import java.lang.instrument.Instrumentation;

/**
 * Agent call-back that stores the {@link Instrumentation} provided by the JVM.
 * 
 * <p>
 * Not to be used directly.
 */
public class InstrumentationGrabber {
    private static volatile Instrumentation instrumentation;

    public static void premain(String agentArgs, Instrumentation inst) {
        if (InstrumentationGrabber.instrumentation != null)
            throw new AssertionError("Already initialized");
        InstrumentationGrabber.instrumentation = inst;
    }

    private static void checkSetup() {
        if (instrumentation != null) {
            throw new IllegalStateException(
                    "Instrumentation is not setup properly. You have to pass "
                            + "-javaagent:path/to/object-explorer.jar to the "
                            + "java interpreter");
        }
    }

    static Instrumentation instrumentation() {
        checkSetup();
        return instrumentation;
    }
}

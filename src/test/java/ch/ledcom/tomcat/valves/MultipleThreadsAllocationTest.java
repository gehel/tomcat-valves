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

import com.google.monitoring.runtime.instrumentation.AllocationRecorder;
import com.google.monitoring.runtime.instrumentation.Sampler;
import org.junit.Before;
import org.junit.Test;

public class MultipleThreadsAllocationTest {

    @Before
    public void initializeAllocationRecorder() {
        AllocationRecorder.addSampler(new Sampler() {
            @Override
            public void sampleAllocation(int count, String desc, Object newObject, long size) {
                if (!Thread.currentThread().getName().equals("main")) {
                    System.out.println(Thread.currentThread().getName() + ":" + newObject.getClass().getName() + ":" + newObject);
                }
            }
        });
    }

    @Test
    public void allocationFromTwoThreadIsRecognizable() throws InterruptedException {
        System.out.println("start");
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Dummy("1");
            }
        }, "t1").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Dummy("2");
            }
        }, "t2").start();
        System.out.println("in progress");
        Thread.sleep(20);
        System.out.println("stop");
    }

    public static void main(String[] args) throws InterruptedException {
        MultipleThreadsAllocationTest test = new MultipleThreadsAllocationTest();
        test.initializeAllocationRecorder();
        test.allocationFromTwoThreadIsRecognizable();
    }

    private static final class Dummy {
        private final String name;

        private Dummy(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}

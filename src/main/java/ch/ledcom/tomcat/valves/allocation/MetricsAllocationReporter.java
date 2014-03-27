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

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by gehel on 27/03/14.
 */
public class MetricsAllocationReporter implements AllocationReporter {

    private final MetricRegistry metrics;
    private final String prefix;

    public MetricsAllocationReporter(MetricRegistry metrics, String prefix) {
        this.metrics = metrics;
        this.prefix = prefix;
    }

    @Override
    public void report(String context, Long totalRequestAllocation) {
        metrics.histogram(computeMetricName(context)).update(totalRequestAllocation);
    }

    private String computeMetricName(String context) {
        return prefix + context;
    }
}

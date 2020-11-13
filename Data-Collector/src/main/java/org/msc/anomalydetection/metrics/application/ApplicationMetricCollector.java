package org.msc.anomalydetection.metrics.application;

import com.moandjiezana.toml.Toml;
import org.msc.anomalydetection.metrics.core.Metric;
import org.msc.anomalydetection.metrics.core.MetricCollector;

import java.util.Map;

public class ApplicationMetricCollector implements MetricCollector {

    Toml serviceInfo = null;

    @Override
    public ApplicationMetricCollector newService(Toml serviceInfo) {

        this.serviceInfo = serviceInfo;
        return this;
    }

    @Override
    public void receive(Metric metric) {

        metric.setLatency(getLatency());
        metric.setThroughput(getThroughput());
    }

    private Map getLatency() {

        Map<String, Long> latency = null;

        return latency;
    }

    private Map getThroughput() {

        Map<String, Integer> throughput = null;

        return throughput;
    }
}

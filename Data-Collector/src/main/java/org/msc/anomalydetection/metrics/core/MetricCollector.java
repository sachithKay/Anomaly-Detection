package org.msc.anomalydetection.metrics.core;

import com.moandjiezana.toml.Toml;

public interface MetricCollector {

     void receive(Metric metric);
     MetricCollector newService(Toml serviceInfo);
}

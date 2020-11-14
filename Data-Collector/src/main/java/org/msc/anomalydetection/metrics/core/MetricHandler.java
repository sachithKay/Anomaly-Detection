package org.msc.anomalydetection.metrics.core;

import java.util.Timer;

public class MetricHandler {

    public static void main(String[] args) {

        Timer timer = new Timer(false);
        MetricCollectorTask collectorTask = new MetricCollectorTask();
        timer.schedule(collectorTask, 0, 10*1000);
    }
}

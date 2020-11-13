package org.msc.anomalydetection.metrics;

import com.moandjiezana.toml.Toml;
import org.msc.anomalydetection.metrics.application.ApplicationMetricCollector;
import org.msc.anomalydetection.metrics.core.Metric;
import org.msc.anomalydetection.metrics.influx.InfluxDBHandler;
import org.msc.anomalydetection.metrics.system.SystemMetricCollector;
import org.msc.anomalydetection.metrics.util.ServiceConfig;

import java.util.List;
import java.util.TimerTask;

public class MetricCollectorTask extends TimerTask {

    @Override
    public void run() {
        // retrieve the service catalog
        // collect application and system metrics
        // push to db
        ServiceConfig serviceConfig = ServiceConfig.getInstance();
        Toml serviceToml = serviceConfig.getServiceToml();
        ApplicationMetricCollector applicationMetricCollector = new ApplicationMetricCollector();
        SystemMetricCollector systemMetricCollector = new SystemMetricCollector();
        InfluxDBHandler dbHandler = new InfluxDBHandler();

        List<Toml> services = serviceToml.getTables("services");
        for (Toml service : services) {
            Metric metric = new Metric();
            metric.setServiceName(service.getString("container_name"));
            metric.setTimeStamp(System.currentTimeMillis());
            applicationMetricCollector.newService(service).receive(metric);
            systemMetricCollector.newService(service).receive(metric);
            dbHandler.writeMetric(metric);
        }
    }
}

package org.msc.anomalydetection.metrics.db.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.apache.log4j.Logger;
import org.msc.anomalydetection.metrics.core.exception.DataCollectorExcpetion;
import org.msc.anomalydetection.metrics.core.Metric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InfluxDBHandler {

    private static Logger Log = Logger.getLogger(InfluxDBHandler.class);

    private String token;
    private String org;
    private String bucket;
    private InfluxDBClient influxDB = null;

    public InfluxDBHandler() {

        token = "aJ-xRsk9yW_5TOX9Jp89Mwo8OUqEMN3KY4yL8xvtIj9YXDMYGr1tLnW2KhTJki0icPkvlByIR67j4C_8QOgx9g==";
        org = "AD-Data";
        bucket = "Service-Performance";
    }

    public InfluxDBHandler(String token, String org, String bucket) {

        this.token = token;
        this.org = org;
        this.bucket = bucket;

    }

    public void writeMetric(Metric metric) {

        Point point = Point.measurement(metric.getServiceName())
                .time(metric.getTimeStamp(), WritePrecision.MS)
                .addField("memory", metric.getMemoryUsage())
                .addField("cpu", metric.getCpuUsage());

        Map<String, Long> latencies = metric.getLatency();
        latencies.forEach((url, latency) -> {
            point.addField(url + ":latency", latency);
        });

        Map<String, Long> throughputs = metric.getThroughput();
        throughputs.forEach((url, throughput) -> {
            point.addField(url + ":throughput", throughput);
        });

        writePoint(point);
    }

    private void writePoint(Point point) {
        try (WriteApi writeApi = getDBConnection().getWriteApi()) {
            writeApi.writePoint(point);
        }
    }

    public void writeMetricByResource(Metric metric) {

        String serviceName = metric.getServiceName();
        Set<String> resources = metric.getLatency().keySet();
        for (String resource : resources) {
            String measurement = serviceName + ":" + resource.replaceFirst("/", "");
            Point point = getMetricPoint(metric, measurement);

            point.addField("memory", metric.getMemoryUsage())
                    .addField("cpu", metric.getCpuUsage())
                    .addField("throughput", (double) metric.getThroughput().get(resource))
                    .addField("latency", (double) metric.getLatency().get(resource));

            Log.info("Metric data for measurement " + measurement + " was successfully written to InfluxDB at " + metric.getTimeStamp());
            writePoint(point);
        }
    }

    public void writeMetrics(Metric metric) {

        List<Point> pointList = new ArrayList<>();

        Point memoryPoint = getMetricPoint(metric, "memory")
                .addField("value", metric.getMemoryUsage());
        pointList.add(memoryPoint);

        Point cpuPoint = getMetricPoint(metric, "cpu")
                .addField("value", metric.getCpuUsage());
        pointList.add(cpuPoint);

        Point latencyPoint = Point.measurement("latency");
        populateLatencies(latencyPoint, metric);
        pointList.add(latencyPoint);

        Point throughputPoint = Point.measurement("throughput");
        populateThroughputs(throughputPoint, metric);
        pointList.add(throughputPoint);

        try (WriteApi writeApi = getDBConnection().getWriteApi()) {
            writeApi.writePoints(pointList);
        }
    }

    private Point getMetricPoint(Metric metric, String measurement) {

        return Point.measurement(measurement)
                .time(metric.getTimeStamp(), WritePrecision.MS)
                .addTag("service", metric.getServiceName());
    }

    private void populateLatencies(Point latencyPoint, Metric metric) {

        Map<String, Long> latencies = metric.getLatency();
        latencies.forEach((url, latency) -> {
            latencyPoint.addField(url, latency);
        });
    }

    private void populateThroughputs(Point throughputPoint, Metric metric) {

        Map<String, Long> throughputs = metric.getThroughput();
        throughputs.forEach((url, throughput) -> {
            throughputPoint.addField(url, throughput);
        });
    }

    private InfluxDBClient getDBConnection() {

        if (this.influxDB == null) {
            establishDBConnection();
        }
        return this.influxDB;
    }

    private void establishDBConnection() {

        influxDB = InfluxDBClientFactory.create("http://localhost:8086", token.toCharArray(), org, bucket);
        if (!isConnectionVerified()) {
            influxDB = null;
            throw new DataCollectorExcpetion("Database connection cannot be established.");
        }
    }

    private boolean isConnectionVerified() {
        // lets do a health check here
        return true;
    }
}

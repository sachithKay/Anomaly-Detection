package org.msc.anomalydetection.metrics.influx;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.msc.anomalydetection.metrics.core.exception.DataCollectorExcpetion;
import org.msc.anomalydetection.metrics.core.Metric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InfluxDBHandler {

    private String token = "wvq-sNwaHCD0sVqUYhOv-TvA0zi8MywnnNWBdAZE0tgDVya0XZDJWp3o-dx45nIjif0758-RaxZWtDUdtdj3vQ==";
    private String org = "AD-Data";
    private String bucket = "Service-Performance";
    private InfluxDBClient influxDB = null;

    public InfluxDBHandler() {

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
                .addField("cpu", metric.getCpuUsage())
                .addField("throughput", 1015096L)
                .addField("latency", 1010467L);

        try (WriteApi writeApi = getDBConnection().getWriteApi()) {
            writeApi.writePoint(point);
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
       Map<String, Long> latencies =  metric.getLatency();
       latencies.forEach((url, latency) -> {
           latencyPoint.addField(url, latency);
       });
    }

    private void populateThroughputs(Point throughputPoint, Metric metric) {
        Map<String, Long> latencies =  metric.getThroughput();
        latencies.forEach((url, latency) -> {
            throughputPoint.addField(url, latency);
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

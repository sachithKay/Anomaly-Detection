package org.msc.anomalydetection.metrics.application;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moandjiezana.toml.Toml;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.msc.anomalydetection.metrics.core.Metric;
import org.msc.anomalydetection.metrics.core.MetricCollector;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationMetricCollector implements MetricCollector {

    Toml serviceInfo = null;
    private static Logger Log = Logger.getLogger(ApplicationMetricCollector.class);

    @Override
    public ApplicationMetricCollector newService(Toml serviceInfo) {

        this.serviceInfo = serviceInfo;
        return this;
    }

    @Override
    public void receive(Metric metric) {

        String actuatorUrl = this.serviceInfo.getString("endpoint") + "/actuator/metrics/http.server.requests?tag=uri:";
        List<Toml> resources = serviceInfo.getTables("resources");
        Map<String, Double> latency = new HashMap<>();
        Map<String, Double> throughput = new HashMap<>();
        for (Toml resource : resources) {
            String path = resource.getString("path");
            Response actuatorResponse = invokeGet(actuatorUrl + path);
            if (actuatorResponse.isSuccessful()) {
                try {
                    String responseString = actuatorResponse.body().string();
                    JsonObject jsonResponse = new JsonParser().parse(responseString).getAsJsonObject();
                    JsonArray measurements = jsonResponse.getAsJsonArray("measurements");
                    latency.put(path, getMaxLatencyWithActuator(measurements));
                    throughput.put(path, getAvgThroughputWithActuator(measurements));
                } catch (IOException e) {
                    Log.error("Error when reading response.", e);
                }
            } else {
                Log.error("Error when calling spring boot actuator with url " + actuatorUrl);
            }
        }
        metric.setLatency(latency);
        metric.setThroughput(throughput);
    }

    /**
     * Calculates latencies by making calls to respective resources
     *
     * @param resource
     * @return resource latency in millis
     */
    private long calculateLatency(Toml resource) {

        long latency = 0;

        String method = resource.getString("method");
        String serviceUrl = this.serviceInfo.getString("endpoint") + resource.getString("path");

        if (method.equalsIgnoreCase("POST")) {
            String payload = resource.getString("payload", "");
            RequestBody body;
            if (!payload.isEmpty()) {
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                JsonObject request = new JsonParser().parse(payload).getAsJsonObject();
                body = RequestBody.create(JSON, request.toString());
                invokePost(serviceUrl, body);
            } else {
                body = RequestBody.create(null, new byte[]{});
            }
            invokePost(serviceUrl, body);
        } else if (method.equalsIgnoreCase("GET")) {
            invokeGet(serviceUrl);
        }
        return latency;
    }

    /**
     * Executes POST calls to a provided URL
     *
     * @param serviceUrl
     * @param body       RequestBody payload
     * @return response
     */
    private Response invokePost(String serviceUrl, RequestBody body) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(serviceUrl).post(body).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Log.error("Error while making request. ", e);
        }
        return response;
    }

    /**
     * Executes GET calls to a provided URL
     *
     * @param serviceUrl
     * @return Response received
     */
    private Response invokeGet(String serviceUrl) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(serviceUrl).build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Log.error("Error while making request. ", e);
        }
        return response;
    }

    /**
     * This method uses spring boot actuator to
     * obtain application latencies
     */
    private double getMaxLatencyWithActuator(JsonArray measurements) {

        return measurements.get(2).getAsJsonObject().get("value").getAsDouble();

    }

    private double getAvgThroughputWithActuator(JsonArray measurements) {
        double avgThroughput = 0;
        int messageCount = measurements.get(0).getAsJsonObject().get("value").getAsInt();
        double totalTime = measurements.get(1).getAsJsonObject().get("value").getAsDouble();
        avgThroughput = messageCount / totalTime;
        return avgThroughput;
    }
}

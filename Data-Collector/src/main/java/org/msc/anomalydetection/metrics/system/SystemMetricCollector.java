
package org.msc.anomalydetection.metrics.system;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moandjiezana.toml.Toml;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.msc.anomalydetection.metrics.core.Metric;
import org.msc.anomalydetection.metrics.core.MetricCollector;

import java.io.IOException;
import java.util.Set;

public class SystemMetricCollector implements MetricCollector {

    private static Logger Log = Logger.getLogger(SystemMetricCollector.class);
    private Toml serviceInfo = null;
    Response cAdvisorResponse = null;

    @Override
    public void receive(Metric metric) {

        // call cAdvisor to get service details
        requestCAdvisor();

        if (cAdvisorResponse != null && cAdvisorResponse.isSuccessful()) {
            try {
                String responseString = cAdvisorResponse.body().string();
                JsonObject jsonResponse = new JsonParser().parse(responseString).getAsJsonObject();
                Set<String> keys = jsonResponse.keySet();

                String statArrayKey = null;
                for (String key: keys) {
                    // here we are only want the one and only element.
                    // hence no need to iterate
                    statArrayKey = key;
                    break;
                }

                JsonArray statArray = jsonResponse.getAsJsonArray(statArrayKey);
                JsonObject statObject = statArray.get(0).getAsJsonObject();

                metric.setCpuUsage(getCpuUsage(statObject));
                metric.setMemoryUsage(getMemoryUsage(statObject));
            } catch (IOException e) {
                Log.error("Error parsing json response.");
            }
        } else {
            Log.error("Response has not been initialized.");
        }
    }

    @Override
    public MetricCollector newService(Toml serviceInfo) {

        this.serviceInfo = serviceInfo;
        return this;
    }

    private long getCpuUsage(JsonObject stats) {

        long cpuUsage = stats.getAsJsonObject("cpu").getAsJsonObject("usage").get("total").getAsLong();
        return cpuUsage;
    }

    private long getMemoryUsage(JsonObject stats) {

        long memoryUsage = stats.getAsJsonObject("memory").get("usage").getAsLong();
        return memoryUsage;
    }

    private void requestCAdvisor() {

        if (serviceInfo != null) {
            String serviceUrl = "http://127.0.0.1:8080/api/v2.0/stats/" + serviceInfo.getString("container_name") + "?type=docker&count=1";
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(serviceUrl).build();
            try {
                cAdvisorResponse = client.newCall(request).execute();
            } catch (IOException e) {
                Log.error("Error while making request. ", e);
            }
        } else {
            Log.error("Service information has not been initialized. Cannot make requests to cAdvisor");
        }
    }
}

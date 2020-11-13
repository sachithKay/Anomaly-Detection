package org.msc.anomalydetection.metrics.core;

import java.util.Map;

public class Metric {

    private String serviceName;
    private long timeStamp;
    private Map<String, Long> latency;
    private Map<String, Integer> throughput;
    private long cpuUsage;
    private long memoryUsage;

    public String getServiceName() {

        return serviceName;
    }

    public void setServiceName(String serviceName) {

        this.serviceName = serviceName;
    }

    public long getTimeStamp() {

        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {

        this.timeStamp = timeStamp;
    }

    public Map getLatency() {

        return latency;
    }

    public void setLatency(Map latency) {

        this.latency = latency;
    }

    public Map getThroughput() {

        return throughput;
    }

    public void setThroughput(Map throughput) {

        this.throughput = throughput;
    }

    public long getCpuUsage() {

        return cpuUsage;
    }

    public void setCpuUsage(long cpuUsage) {

        this.cpuUsage = cpuUsage;
    }

    public long getMemoryUsage() {

        return memoryUsage;
    }

    public void setMemoryUsage(long memoryUsage) {

        this.memoryUsage = memoryUsage;
    }
}

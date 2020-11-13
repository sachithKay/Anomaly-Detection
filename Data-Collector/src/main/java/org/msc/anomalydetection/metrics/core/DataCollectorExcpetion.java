package org.msc.anomalydetection.metrics.core;

public class DataCollectorExcpetion extends RuntimeException {

    public DataCollectorExcpetion(String message) {
        super(message);
    }

    public DataCollectorExcpetion(String message, Throwable error) {
        super(message, error);
    }
}

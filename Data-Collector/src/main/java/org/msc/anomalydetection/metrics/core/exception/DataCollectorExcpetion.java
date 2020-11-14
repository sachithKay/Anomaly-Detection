package org.msc.anomalydetection.metrics.core.exception;

public class DataCollectorExcpetion extends RuntimeException {

    public DataCollectorExcpetion(String message) {
        super(message);
    }

    public DataCollectorExcpetion(String message, Throwable error) {
        super(message, error);
    }
}

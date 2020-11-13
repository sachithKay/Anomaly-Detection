package org.msc.anomalydetection.metrics.util;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.nio.file.Paths;

public class ServiceConfig {

    private static ServiceConfig serviceConfig = new ServiceConfig();
    private Toml serviceConfiguration;

    private ServiceConfig() {

        this.serviceConfiguration = loadConfiguration();
    }

    public static ServiceConfig getInstance() {

        return serviceConfig;
    }

    public Toml getServiceToml() {

        return serviceConfiguration;
    }

    private Toml loadConfiguration() {

        Toml configToml = new Toml().read(getConfigFile());
        return configToml;
    }

    private File getConfigFile() {

        String configFilePath = Paths.get(System.getProperty("user.dir"), "service.toml").toString();
        File configFile = new File(configFilePath);
        return configFile;
    }
}

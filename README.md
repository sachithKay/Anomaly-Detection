# Anomaly-Detection

This repository contains implementation of an Anomaly detection in micro-services. 
- **Data-Collector**: This module contains a java implementation of obtaining storing service metrics used for anomaly detection.
- **Microservices**: This module contains a set of dockerized micro-services. This is used to create a data set to train the model.

## Building from the source

- Build from the parent module 'Anomaly-Detection' `mvn clean install` for a normal build. However this will not create docker images
of the micro services.
- If you want to create docker images of the services as well execute the `mvn clean install -Ddocker` which will run the docker build
profile of the services, creating the images. 


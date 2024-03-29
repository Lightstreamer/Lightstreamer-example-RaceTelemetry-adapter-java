# Lightstreamer - Race Telemetry Demo - Java Adapter

<!-- START DESCRIPTION lightstreamer-example-racetelemetry-adapter-java -->

This project shows the Race Telemetry Demo Data Adapters and how they can be plugged into Lightstreamer Server and used to feed the [Lightstreamer - Race Telemetry Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-RaceTelemetry-client-javascript) front-end.
The *Race Telemetry Demo* is a simple real-time telemetry application based on Lightstreamer.

## Details

The project is comprised of source code and a deployment example.

### Dig the Code

#### Race Telemetry Data Adapter
Contains the source code for the Data Adapter. The data is generated by a random feed simulator of the telemetry metrics of a car such as Time, Distance, Speed, Engine RPM, Gear, and Laps.<br>

<br>
The Metadata Adapter functionalities are absolved by the `LiteralBasedProvider` in [Lightstreamer Java In-Process Adapter SDK](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-inprocess#literalbasedprovider-metadata-adapter), a simple full implementation of a Metadata Adapter, already provided by Lightstreamer server. 
<br>
See the source code comments for further details.

<!-- END DESCRIPTION lightstreamer-example-racetelemetry-adapter-java -->

### The Adapter Set Configuration

This Adapter Set is configured and will be referenced by the clients as `F1Telemetry`. 

The `adapters.xml` file for the *Race Telemetry Demo*, should look like:
```xml      
  <?xml version="1.0"?>

 <adapters_conf id="F1Telemetry">

    <metadata_provider>
      <adapter_class>com.lightstreamer.adapters.metadata.LiteralBasedProvider</adapter_class>
      
      <param name="search_dir">.</param>
      <param name="static">Y</param>
      <param name="item_family_1">L_driver_1</param>
      <param name="modes_for_item_family_1">DISTINCT</param>
      <param name="item_family_2">P_driver_1</param>
      <param name="modes_for_item_family_2">MERGE</param>
    </metadata_provider>
    
    <data_provider name="FORMULA1_ADAPTER">
      <adapter_class>com.lightstreamer.examples.race_telemetry_demo.DataProviderImpl</adapter_class>
      <param name="log_config_refresh_seconds">10</param>
    </data_provider>
    
  </adapters_conf>
```

<i>NOTE: not all configuration options of an Adapter Set are exposed by the file suggested above. 
You can easily expand your configurations using the generic template, see the [Java In-Process Adapter Interface Project](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-inprocess#configuration) for details.</i><br>
<br>
Please refer [here](https://lightstreamer.com/docs/ls-server/latest/General%20Concepts.pdf) for more details about Lightstreamer Adapters.<br>

## Install

If you want to install a version of the *Race Telemetry Demo* in your local Lightstreamer Server, follow these steps.

* Download *Lightstreamer Server* (Lightstreamer Server comes with a free non-expiring demo license for 20 connected users) from [Lightstreamer Download page](http://www.lightstreamer.com/download.htm), and install it, as explained in the `GETTING_STARTED.TXT` file in the installation home directory.
* Make sure that Lightstreamer Server is not running.
* Get the `deploy.zip` file of the [latest release](https://github.com/Lightstreamer/Lightstreamer-example-RaceTelemetry-adapter-java/releases), unzip it, and copy the `RaceTelemetry` folder into the `adapters` folder of your Lightstreamer Server installation.
* Launch Lightstreamer Server.
* Test the Adapter, launching the [Lightstreamer - Race Telemetry Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-RaceTelemetry-client-javascript) listed in [Clients Using This Adapter](https://github.com/Lightstreamer/Lightstreamer-example-RaceTelemetry-adapter-java#clients-using-this-adapter).

## Build

To build your own version of `racetelemetry-adapter-java-x.y.z.jar` instead of using the one provided in the `deploy.zip` file from the [Install](https://github.com/Lightstreamer/Lightstreamer-example-RaceTelemetry-adapter-java#install) section above, you have two options:
either use [Maven](https://maven.apache.org/) (or other build tools) to take care of dependencies and building (recommended) or gather the necessary jars yourself and build it manually.
For the sake of simplicity only the Maven case is detailed here.

### Maven

You can easily build and run this application using Maven through the pom.xml file located in the root folder of this project. As an alternative, you can use an alternative build tool (e.g. Gradle, Ivy, etc.) by converting the provided pom.xml file.

Assuming Maven is installed and available in your path you can build the demo by running
```sh 
 mvn install dependency:copy-dependencies 
```

## See Also

### Clients Using This Adapter
<!-- START RELATED_ENTRIES -->

* [Lightstreamer - Race Telemetry Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-RaceTelemetry-client-javascript)

<!-- END RELATED_ENTRIES -->

### Related Projects

* [LiteralBasedProvider Metadata Adapter](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-inprocess#literalbasedprovider-metadata-adapter)

## Lightstreamer Compatibility Notes


- Compatible with Lightstreamer SDK for Java In-Process Adapters since 7.3.
- For a version of this example compatible with Lightstreamer SDK for Java Adapters version 6.0, please refer to [this tag](https://github.com/Lightstreamer/Lightstreamer-example-RaceTelemetry-adapter-java/tree/pre_mvn).


# Lightstreamer - Race Telemetry Demo - Java Adapter #
<!-- START DESCRIPTION Race Telemetry Demo -->

This project shows the Race Telemetry Demo Data Adapters and how they can be plugged into Lightstreamer Server and used to feed the [Lightstreamer - Race Telemetry Demo - HTML Client](https://github.com/Weswit/Lightstreamer-example-RaceTelemetry-client-javascript) front-end. Please refer [here](http://www.lightstreamer.com/latest/Lightstreamer_Allegro-Presto-Vivace_5_1_Colosseo/Lightstreamer/DOCS-SDKs/General%20Concepts.pdf) for more details about Lightstreamer Adapters.<br>
The [Lightstreamer - Race Telemetry Demo - HTML Client ](https://github.com/Weswit/Lightstreamer-example-RaceTelemetry-client-javascript) is a simple real-time telemetry application based on Lightstreamer.

The project is comprised of source code and a deployment example.

## Race Telemetry Data Adapter ##
Contains the source code for the Data Adapter. The data is generated by a random feed simulator of the telemetry metrics of a car such as: Time, Distance, Speed, Engine RPM, Gear, and Laps.<br>

<br>
The Metadata Adapter functionalities are absolved by the  `LiteralBasedProvider` in [Lightstreamer - Reusable Metadata Adapters - Java Adapter](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java), a simple full implementation of a Metadata Adapter, made available in Lightstreamer distribution. 
<br>
See the source code comments for further details.
<!-- END DESCRIPTION Race Telemetry Demo -->

# Build #

If you want to skip the build process of this Adapter please note that in the [deploy](https://github.com/Weswit/Lightstreamer-example-RaceTelemetry-adapter-java/releases) release of this project you can find the "deploy.zip" file that contains a ready-made deployment resource for the Lightstreamer server.
Otherwise follow these steps:

*  Get the ls-adapter-interface.jar, and log4j-1.2.15.jar files from the [latest Lightstreamer distribution](http://www.lightstreamer.com/download).
*  Create the LS_WebTelemetry_DataAdapter.jar file with commands like these:
```sh
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath compile_libs/log4j-1.2.15.jar;compile_libs/ls-adapter-interface/ls-adapter-interface.jar; -sourcepath src -d tmp_classes src/com/lightstreamer/adapters/web_telemetry/DataProviderImpl.java
 
 >jar cvf LS_WebTelemetry_DataAdapter.jar -C tmp_classes src
```

# Deploy #

Now you are ready to deploy the Race Telemetry Demo Adapter into Lighstreamer server.<br>
After you have Downloaded and installed Lightstreamer, please go to the "adapters" folder of your Lightstreamer Server installation.

You have to create a specific folder to deploy the Race Telemetry Adapters otherwise get the ready-made "RaceTelemetry" deploy folder from "deploy.zip" of the [latest release](https://github.com/Weswit/Lightstreamer-example-RaceTelemetry-adapter-java/releases) of this project and skips the next three steps.

1. Create a new folder, let's call it "RaceTelemetry", and a "lib" folder inside it.
2. Create an "adapters.xml" file inside the "RaceTelemetry" folder and use the following content (this is an example configuration, you can modify it to your liking):

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
    
    <data_provider>
      <adapter_class>com.lightstreamer.adapters.web_telemetry.DataProviderImpl</adapter_class>
      <param name="log_config">adapters_log_conf.xml</param>
      <param name="log_config_refresh_seconds">10</param>
    </data_provider>
    
  </adapters_conf>
```
<br> 
3. Copy into "/RaceTelemetry/lib" the jar LS_WebTelemetry_DataAdapter.jar created in the previous section.

Now your "RaceTelemetry" folder is ready to be deployed in the Lightstreamer server, please follow these steps:<br>

1. Make sure you have installed Lightstreamer Server, as explained in the GETTING_STARTED.TXT file in the installation home directory.
2. Make sure that Lightstreamer Server is not running.
3. Copy the "RaceTelemetry" directory and all of its files to the "adapters" subdirectory in your Lightstreamer Server installation home directory.
4. Lightstreamer Server is now ready to be launched.

Please test your Adapter with one of the clients in the [list](https://github.com/Weswit/Lightstreamer-example-RaceTelemetry-adapter-java#clients-using-this-adapter) below.

# See Also #

## Clients using this Adapter ##
<!-- START RELATED_ENTRIES -->

* [Lightstreamer - Race Telemetry Demo - HTML Client](https://github.com/Weswit/Lightstreamer-example-RaceTelemetry-client-javascript)

<!-- END RELATED_ENTRIES -->

## Related projects ##

* [Lightstreamer - Reusable Metadata Adapters - Java Adapter](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java)

# Lightstreamer Compatibility Notes #

- Compatible with Lightstreamer SDK for Java Adapters since 5.1

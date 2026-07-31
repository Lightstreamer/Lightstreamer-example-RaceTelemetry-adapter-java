/*
 *
 * Copyright (c) Lightstreamer Srl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
 
package com.lightstreamer.examples.race_telemetry_demo.adapters;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lightstreamer.adapter.common.InitException;
import com.lightstreamer.adapter.common.Mode;
import com.lightstreamer.adapter.data.DataAdapter;
import com.lightstreamer.adapter.data.ItemListener;
import com.lightstreamer.adapter.data.ItemManager;
import com.lightstreamer.adapter.data.SubscriptionException;

public class DataProviderImpl implements DataAdapter {

    /**
     * Private logger; a specific "LS_demos_Logger.WebTelemetry" category
     * should be supplied by log4j configuration.
     */
	private Logger logger;
    public static final  String LOGGER_NAME = "LS_demos_Logger.WebTelemetry";
	
    private Properties lsProperties=new Properties();
	
    private Random random;
    private HashMap<String, DataGenerator> items=new HashMap<>();
    

    public DataProviderImpl() {
        random = new Random();
    }

    public void init(Map<String, String> params, File configDir) throws InitException {
    	try {
            logger = LogManager.getLogger(LOGGER_NAME);
    	    logger.info("WebTelemetryF1 ready");
		} catch (Exception e) {
			System.out.println("UNABLE TO CONFIGURE WEB TELEMETRY APPLICATION!!!:"+e.getMessage());
		} 	
    }

    @Override
    public CompletionStage<ItemManager> getItemManager(String itemName)
            throws SubscriptionException {
        String[] splits = itemName.split("_");

        if (!splits[0].equals("L") && !splits[0].equals("P")) {
            throw new SubscriptionException("Item not supported: " + itemName);
        }

        Mode mode = splits[0].equals("L") ? Mode.DISTINCT : Mode.MERGE;

        return CompletableFuture.completedFuture(new ItemManager() {

            @Override
            public Mode getSupportedMode() {
                return mode;
            }

            @Override
            public Integer getDistinctSnapshotLength() {
                return mode == Mode.DISTINCT ? 30 : null;
            }

            @Override
            public CompletionStage<Void> subscribe(ItemListener listener) {
                String[] subSplits = itemName.split("_");
                String subKey = itemName.substring(itemName.indexOf("_"));
                DataGenerator dataGenerator = null;

                logger.debug("Susbcribe received key: " + subKey + "; item type: " + subSplits[0] + ".");

                synchronized (items) {
                    if ( items.containsKey(subKey) ) {
                        dataGenerator = items.get(subKey);
                    } else {
                        dataGenerator = new DataGenerator(subKey);
                        items.put(subKey, dataGenerator);
                    }
                }

                if (subSplits[0].equals("L")) {
                    dataGenerator.setL_listener(listener);
                } else if (subSplits[0].equals("P")) {
                    dataGenerator.setP_listener(listener);
                }

                listener.endOfSnapshot();
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public boolean isSnapshotAvailable() {
                return true;
            }

            @Override
            public CompletionStage<Void> unsubscribe() {
                String[] unSplits = itemName.split("_");
                String unKey = itemName.substring(itemName.indexOf("_"));
                DataGenerator dataGenerator = null;

                logger.debug("Unsusbcribe received key: " + unKey + "; item type: " + unSplits[0] + ".");

                synchronized (DataProviderImpl.this) {
                    synchronized (items) {
                        if ( items.containsKey(unKey) ) {
                            dataGenerator = items.get(unKey);

                            if (unSplits[0].equals("L")) {
                                dataGenerator.setL_listener(null);
                            } else if (unSplits[0].equals("P")) {
                                dataGenerator.setP_listener(null);
                            } else {
                                // skip.
                            }

                            if (dataGenerator.isTerminable()) {

                                logger.debug("Stop race!");

                                dataGenerator.close();
                                dataGenerator.interrupt();
                                items.remove(unKey);
                            }
                        }
                    }
                }
                return CompletableFuture.completedFuture(null);
            }
        });
    }

    @Override
    public void shutdown() {
        synchronized (items) {
            for (DataGenerator dataGenerator : items.values()) {
                dataGenerator.close();
                dataGenerator.interrupt();
            }
            items.clear();
        }
    }

}

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
 
package com.lightstreamer.examples.race_telemetry_demo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lightstreamer.interfaces.data.SmartDataProvider;
import com.lightstreamer.interfaces.data.FailureException;
import com.lightstreamer.interfaces.data.ItemEventListener;
import com.lightstreamer.interfaces.data.SubscriptionException;

public class DataProviderImpl implements SmartDataProvider {

    /**
     * Private logger; a specific "LS_demos_Logger.WebTelemetry" category
     * should be supplied by log4j configuration.
     */
	private Logger logger;
    public static final  String LOGGER_NAME = "LS_demos_Logger.WebTelemetry";
	
    private Properties lsProperties=new Properties();
	
	private ItemEventListener listener;
    private Random random;
    private HashMap items=new HashMap();
    

    public DataProviderImpl() {
        random = new Random();
    }

    public void init(Map params, File configDir) {
    	try {
            logger = LogManager.getLogger(LOGGER_NAME);
    	    logger.info("WebTelemetryF1 ready");
		} catch (Exception e) {
			System.out.println("UNABLE TO CONFIGURE WEB TELEMETRY APPLICATION!!!:"+e.getMessage());
		} 	
    }

    
    public void setListener(ItemEventListener listener) {
        this.listener = listener;
    }

    public synchronized void unsubscribe(String itemName) {
        String[] splits = itemName.split("_"); 
        String key = itemName.substring(itemName.indexOf("_"));
        DataGenerator dataGenerator = null;
        
        logger.debug("Unsusbcribe received key: " + key + "; item type: " + splits[0] + ".");
        
        synchronized (items) {
            if ( items.containsKey(key) ) {
                dataGenerator = (DataGenerator) items.get(key);
                
                if (splits[0].equals("L")) {
                    dataGenerator.setL_handle(null);
                } else if (splits[0].equals("P")) {
                    dataGenerator.setP_handle(null);
                } else {
                    // skip.
                }
                
                if (dataGenerator.isTerminable()) {
            
                    logger.debug("Stop race!");
                    
                    dataGenerator.close();
                    dataGenerator.interrupt();
                    items.remove(key);
                }
            }
        }
    }

    public boolean isSnapshotAvailable(String itemName) {
        return true;
    }

	/* (non-Javadoc)
	 * @see com.lightstreamer.interfaces.data.DataProvider#subscribe(java.lang.String, boolean)
	 */
	public void subscribe(String itemName, boolean needsIterator) throws SubscriptionException, FailureException {
	    throw new SubscriptionException("Method not supported.");
	}
	
	public void subscribe(String itemName, Object itemHandle, boolean needsIterator) throws SubscriptionException {
	    String[] splits = itemName.split("_"); 
	    String key = itemName.substring(itemName.indexOf("_"));
	    DataGenerator dataGenerator = null;
	    
	    logger.debug("Susbcribe received key: " + key + "; item type: " + splits[0] + ".");
	    
	    synchronized (items) {
    	    if ( items.containsKey(key) ) {
    	        dataGenerator = (DataGenerator) items.get(key); 
    	    } else {
    	        dataGenerator = new DataGenerator(key,listener);
    	        items.put(key, dataGenerator);
    	    }
	    }
	    
	    if (splits[0].equals("L")) {
	        dataGenerator.setL_handle(itemHandle);
	    } else if (splits[0].equals("P")) {
	        dataGenerator.setP_handle(itemHandle);
	    } else {
	        throw new SubscriptionException("Item not supported.");
	    }
	    
	}
	
  

}

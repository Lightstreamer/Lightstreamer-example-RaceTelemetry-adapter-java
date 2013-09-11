/*
 *
 * Copyright 2013 Weswit s.r.l.
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
 
package com.lightstreamer.adapters.web_telemetry;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.lightstreamer.interfaces.data.DataProvider;
import com.lightstreamer.interfaces.data.FailureException;
import com.lightstreamer.interfaces.data.ItemEventListener;
import com.lightstreamer.interfaces.data.SubscriptionException;

public class DataProviderImpl implements DataProvider {

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
            String logConfig = (String) params.get("log_config");
            if (logConfig != null) {
                File logConfigFile = new File(configDir, logConfig);
                String logRefresh = (String) params.get("log_config_refresh_seconds");
                if (logRefresh != null) {
                    DOMConfigurator.configureAndWatch(logConfigFile.getAbsolutePath(), Integer.parseInt(logRefresh) * 1000);
                } else {
                    DOMConfigurator.configure(logConfigFile.getAbsolutePath());
                }
            }
    	    logger = Logger.getLogger(LOGGER_NAME);

    	    logger.log(Level.INFO, "WebTelemetryF1 Pronto");
		} catch (Exception e) {
			System.out.println("UNABLE TO CONFIGURE WEB TELEMETRY APPLICATION!!!:"+e.getMessage());
		} 	
    }

    
    public void setListener(ItemEventListener listener) {
        this.listener = listener;
    }

    public synchronized void unsubscribe(String itemName) {
    	logger.debug("Chiamata UNsubscribe, itemName: "+itemName);
        
    	DataGenerator dataGenerator= (DataGenerator) items.get(itemName);
        if (dataGenerator != null) {
        	dataGenerator.close();
        	dataGenerator.interrupt();
            items.remove(itemName);
        }

    }

    public boolean isSnapshotAvailable(String itemName) {
        return true;
    }

	/* (non-Javadoc)
	 * @see com.lightstreamer.interfaces.data.DataProvider#subscribe(java.lang.String, boolean)
	 */
	public void subscribe(String itemName, boolean needsIterator) throws SubscriptionException, FailureException {
	    DataGenerator dataGenerator = new DataGenerator(itemName,listener);
	    
	     
	     items.put(itemName, dataGenerator);
		
	}
	
  

}

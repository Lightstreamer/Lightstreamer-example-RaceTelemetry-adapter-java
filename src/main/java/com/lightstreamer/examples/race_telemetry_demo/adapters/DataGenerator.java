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

import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lightstreamer.adapter.data.ItemListener;

public class DataGenerator extends Thread {

	private volatile ItemListener l_listener=null;
	private volatile ItemListener p_listener=null;
	private String key=null;
	
	private Logger logger = LogManager.getLogger(DataProviderImpl.LOGGER_NAME);
	
	private static final int millis=100;
	private boolean closed = false;
	private boolean started = false;
	   
	 
	public DataGenerator(String key)
	{   logger.debug("Driver: "+key);
		
		this.key=key;
		
	}//DataGenerator
	
   public void setL_listener(ItemListener l_listener) {
        this.l_listener = l_listener;
    }

    public void setP_listener(ItemListener p_listener) {
        this.p_listener = p_listener;
        if ( !started ) {
            this.start();
            started = true;
        }
    }
	
    public boolean isTerminable() {
        if ( (p_listener == null) && (l_listener == null) ) {
            return true;
        }
        
        return false;
    }
    
	public void run()
	{  
		int lap = 1;
		long lapStart=(new Date()).getTime();
		PacketItem packetItem=null;
		LapItem lapItem=null;
		
		logger.debug("Start race!");
		
		while(! closed)
		{ 
		  packetItem=new PacketItem(packetItem,lapStart);
		  int currLap = Integer.parseInt((String)packetItem.item.get(PacketItem.FIELDS[6]));
		  
		  if (currLap > lap) {
		  	// ASSERT (currLap == lap+1)
		  	long time=Long.parseLong((String)packetItem.item.get(PacketItem.FIELDS[0]));
		  	// time should be corrected by removing the first piece of the new lap
		  	lapStart += time;
		  	packetItem.item.put(PacketItem.FIELDS[0],String.valueOf(0));
		  	
		  	lap = currLap;
		  
		  	lapItem=new LapItem(lapItem,time);
		  	if (l_listener != null) {
	  	        l_listener.update(lapItem.item);
		  	}
					
		  }
		  
		  if (p_listener != null) {
	          p_listener.update(packetItem.item);
		  }
			
		    try {
				sleep(millis);
			} catch (InterruptedException e) {
				//exit from the loop.
				break;
			}
		}
	}//run
	
	public void close() {
		closed = true;
	}
	
}
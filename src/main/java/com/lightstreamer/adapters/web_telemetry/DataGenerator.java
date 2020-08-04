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
 
package com.lightstreamer.adapters.web_telemetry;

import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lightstreamer.interfaces.data.ItemEventListener;

public class DataGenerator extends Thread {

	private ItemEventListener listener=null;

    private Object L_handle=null;
	private Object P_handle=null;
	private String key=null;
	
	private Logger logger = LogManager.getLogger(DataProviderImpl.LOGGER_NAME);
	
	private static final int millis=100;
	private boolean closed = false;
	private boolean started = false;
	   
	 
	public DataGenerator(String key, ItemEventListener listener)
	{   logger.debug("Driver: "+key);
		
		this.key=key;
		this.listener=listener;
			
	}//DataGenerator
	
   public void setL_handle(Object l_handle) {
        L_handle = l_handle;
    }

    public void setP_handle(Object p_handle) {
        P_handle = p_handle;
        if ( !started ) {
            this.start();
            started = true;
        }
    }
	
    public boolean isTerminable() {
        if ( (P_handle == null) && (L_handle == null) ) {
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
		  	if (L_handle != null) {
		  	    listener.smartUpdate(L_handle,lapItem,false);
		  	}
					
		  }
		  
		  if (P_handle != null) {
		      listener.smartUpdate(P_handle,packetItem,false);
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
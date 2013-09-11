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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.apache.log4j.Logger;

import com.lightstreamer.interfaces.data.ItemEvent;

public class PacketItem implements ItemEvent {
	HashMap item = null;
		

	public static final String[] FIELDS = { "TIME", "Distance", "Speed", "Rpm",
			"Gear_output", "ABS", "LAP" };

	public static final int FIELDS_NRO = FIELDS.length;

	private static Logger logger = Logger.getLogger(DataProviderImpl.LOGGER_NAME);

	private static final Random random = new Random();
	private static int ref=215;
	
	private static double limit = 135;     // Max difference compared to ref. price
	public static double lapLenght = 4.8d; // Length of a virtual lap
	
	private static int startABS=11323;
	
	private static int[] soglieMarcia = { 90, 150, 220, 300, 320 };
	private static int[] rapportiMarcia = { 250, 400, 300, 250, 1000, 660 };
	private static int giriMin = 2000;
	
	/**
	 * @param packetItem
	 * @param simulationStart
	 */
	public PacketItem(PacketItem packetItem, long simulationStart) {
		item = new HashMap();
		if(packetItem==null)
		{	//TIME
			item.put(FIELDS[0], String.valueOf(0));

			//DISTANCE
			item.put(FIELDS[1], String.valueOf(0));
			
			//SPEED
			item.put(FIELDS[2], String.valueOf(130));
			
			//ENGINE RPM
			item.put(FIELDS[3], String.valueOf(18000));
			
			//GEAR
			item.put(FIELDS[4], String.valueOf(2));
			
			//ABS
			item.put(FIELDS[5], String.valueOf(startABS+1));
			
			//LAP
			item.put(FIELDS[6], String.valueOf(1));
			
			
		} else
		{	long currentTime=(new Date()).getTime()-simulationStart;
			
			// Set the time
			item.put(FIELDS[0], String.valueOf(currentTime));
			
			
			
			// Set the speed
			String lastString=(String)packetItem.item.get(FIELDS[2]);
            int last=Integer.parseInt(lastString);
            int jump = ref / 100; // quanto 
            double relDist = (last - ref) / limit;
            int direction = 1;
            if (relDist < 0) {
                direction = -1;
                relDist = - relDist;
            }
            if (relDist > 1) {
                relDist = 1;
            }
            double weight = (relDist * relDist * relDist);
            double prob = (1 - weight) / 2;
            boolean goFarther = random.nextDouble() < prob;
            if (! goFarther) {
                direction *= -1;
            }
            int difference = uniform(0, jump) * direction;
            int speed = last + difference;
    		item.put(FIELDS[2], String.valueOf(speed));
    		
    		int i;
    		for (i = 0; i < soglieMarcia.length; i++) {
    			if (speed < soglieMarcia[i]) {
    				break;
    			}
    		}
    		int diff;
    		if (i == 0) {
    			diff = speed;
    		} else {
    			diff = (speed - soglieMarcia[i - 1]);
    		}
    		int giri = giriMin + rapportiMarcia[i] * diff;

			// Set the laps
			item.put(FIELDS[3], String.valueOf(giri));
			
			// Set the gear
			item.put(FIELDS[4], String.valueOf(i + 1));
			
    	// Set distance
			String currentTimeString=(String)packetItem.item.get(FIELDS[0]);
			long deltaT=currentTime-Long.parseLong(currentTimeString);
			
			String currentDistanceString=(String)packetItem.item.get(FIELDS[1]);
			double currentDistance= Double.parseDouble(currentDistanceString)+((double)speed)*deltaT/1000/3600;
			item.put(FIELDS[1], String.valueOf(currentDistance));


			//Set lap and ABS
			int lap=((int) Math.floor(currentDistance/lapLenght))+1;
			item.put(FIELDS[6], String.valueOf(lap));
    		
			item.put(FIELDS[5], String.valueOf(startABS+lap));
    		
			
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lightstreamer.interfaces.data.ItemEvent#getNames()
	 */
	public Iterator getNames() {
		return item.keySet().iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.lightstreamer.interfaces.data.ItemEvent#getValue(java.lang.String)
	 */
	public Object getValue(String fieldName) {
		return item.get(fieldName);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < FIELDS_NRO; i++)
			sb.append(FIELDS[i] + ": " + this.getValue(FIELDS[i]));
		return sb.toString();

	}

	public static String randomString(int length) {
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < length; i++) {
			sb.append((char) Math.round(97 + random.nextFloat() * 25));
		}
		return sb.toString();

	}//randomString

    int uniform(int min, int max) {
        int base = random.nextInt(max + 1 - min);
        return base + min;
    }
	
	
}
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

import java.util.HashMap;
import java.util.Iterator;

import com.lightstreamer.interfaces.data.ItemEvent;

public class LapItem implements ItemEvent {
	HashMap item = null;
	public static final String[] FIELDS={"lap","lapTime","avgSpeed","avgSpeedTot"};
	public static final int FIELDS_NRO = FIELDS.length;

	public LapItem()
	{ 	this.item = new HashMap();
	}
	
	public LapItem(HashMap item)
	{ 	this.item = item;
	}
	
	public LapItem(String[] fieldsValue) 
	{
		item = new HashMap();
		for(int i=0;i<fieldsValue.length;i++)
		item.put(FIELDS[i], fieldsValue[i]);
	}

	public LapItem(float[] fieldsValue) 
	{
		item = new HashMap();
		for(int i=0;i<fieldsValue.length;i++)
		item.put(FIELDS[i], String.valueOf(fieldsValue[i]));
	}

	
	public LapItem(LapItem lapItem,long time) 
	{
		item = new HashMap();
		if(lapItem==null)
		{ item.put(FIELDS[0],String.valueOf(1));
		  item.put(FIELDS[1],String.valueOf(time));
		
		  double avgSpeed=PacketItem.lapLenght/time*1000*3600;
		  item.put(FIELDS[2],String.valueOf(avgSpeed));
		  item.put(FIELDS[3],String.valueOf(avgSpeed));
		} else
		{  
			String lapString=(String)lapItem.item.get(FIELDS[0]);
			int lap=Integer.parseInt(lapString);
			item.put(FIELDS[0],String.valueOf(lap+1));
			
			item.put(FIELDS[1],String.valueOf(time));
			
			double avgSpeed=PacketItem.lapLenght/time*1000*3600;
			
			item.put(FIELDS[2],String.valueOf(avgSpeed));
			
			String avgSpeedTotString=(String)lapItem.item.get(FIELDS[3]);
			double avgSpeedTot=Double.parseDouble(avgSpeedTotString);
			double tPregresso = (lap * PacketItem.lapLenght) / avgSpeedTot;
			double tCumul = tPregresso + ((double)time)/1000/3600;
			avgSpeedTot = ((lap+1) * PacketItem.lapLenght) / tCumul;
			item.put(FIELDS[3],String.valueOf(avgSpeedTot));
			
			
			}

		
	}

	
	
	public void setValue(String field,String value)
	{ item.put(field,value);
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

	public String toString()
	{ StringBuffer sb=new StringBuffer();
	 for(int i=0;i<FIELDS_NRO;i++)
	 	sb.append(FIELDS[i]+": "+this.getValue(FIELDS[i])+"\n");
	 return sb.toString();
	 
	}
}
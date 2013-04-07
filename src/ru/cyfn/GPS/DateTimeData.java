package ru.cyfn.GPS;

import java.util.*;

class DateTimeData extends DataContent {
	
	private Calendar cal;
	
	public DateTimeData(byte[] rawData) {
		if(rawData[1] != 0) {						// check if date present
			cal = Calendar.getInstance();
			cal.set(((int)rawData[0])&0xFF + 2000,
					((int)rawData[1])&0xFF, 
					((int)rawData[2])&0xFF, 
					((int)rawData[3])&0xFF, 
					((int)rawData[4])&0xFF, 
					((int)rawData[5])&0xFF);
		}
	}
	
	public byte[] toRawBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	public LinkedHashMap<String, String> getDataContent() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		if (cal != null) result.put("Date & Time",String.format("%tY-%<tm-%<td %<tH:%<tM:%<tS",cal.getTime()));
		return result;
	}

}

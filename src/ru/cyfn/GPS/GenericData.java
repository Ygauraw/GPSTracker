package ru.cyfn.GPS;

import java.util.*;

class GenericData extends GPSDataContent {
	private byte[] data;
	
	public GenericData(byte[] rawData) {
		data = rawData;
	}
	
	public byte[] toRawBytes() {
		return data;
	}
	
	private String dataToString() {
		String result = "";
		for (int i = 0; i < data.length; i++) {
			result += String.format("0x%X,", data[i]);
		}
		return result;
	}
	
	public LinkedHashMap<String, String> getDataContent() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("Generic Data", dataToString());
		return result;
	}	
}
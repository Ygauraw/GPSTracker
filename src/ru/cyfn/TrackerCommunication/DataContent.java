package ru.cyfn.TrackerCommunication;

import java.util.*;

public class DataContent {
	private byte[] data;
	
	public DataContent(byte[] rawData) {
		data = rawData;
	}
	
	public byte[] toRawBytes() {
		return data;
	}
	
	public LinkedHashMap<String, String> getDataContent() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("Generic Data", dataToString());
		return result;
	}
	
	private String dataToString() {
		String result = "";
		for (int i = 0; i < data.length; i++) {
			result += String.format("0x%X,", data[i]);
		}
		return result;
	}
	
	protected int convertBytesToIntUnsigned(byte[] bytes) {
		int result = 0;
		if(bytes == null || bytes.length > 4) return result;
		for (int i = 0; i < bytes.length; i++) {
			result = result << 8;
			result = result | ((int)bytes[i] & 0xFF);
		}
		return result;
	}
}
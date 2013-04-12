package ru.cyfn.TrackerCommunication;

import java.util.*;

public abstract class DataPackage {
	private byte[] rawContent;
	// returns bytes of data package. It's called when data should be sent over network
	public abstract byte[] getRawContent();
	// returns data of a package in form of property list
	public Map<String, String> getDataContent() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("Generic Data", dataToString());
		return result;
	}
	private String dataToString() {
		String result = "";
		for (int i = 0; i < rawContent.length; i++) {
			result += String.format("0x%X,", rawContent[i]);
		}
		return result;
	}
	// returns a new concrete DataPackage
	//protected abstract void constructDataPackage(byte[] data);
	public DataPackage(byte[] data) {
		rawContent = Arrays.copyOf(data, data.length);
	};
	// return a standard response if one needed
	public abstract GPSDataPackage createResponse();
}
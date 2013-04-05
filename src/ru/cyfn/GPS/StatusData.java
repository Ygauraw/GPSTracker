package ru.cyfn.GPS;

import java.util.*;

/*
 * LoginData
 * Device ID - 8 chars
 * Type identity code - 2 chars
 */

class StatusData implements GPSDataContent {
	private byte deviceInfo;
	private byte voltageDegree;
	private byte GSMSignalStrenth;
	
	public StatusData(byte[] rawData) {
		deviceInfo = rawData[0];
		voltageDegree = rawData[1];
		GSMSignalStrenth = rawData[2];
	}
	
	public byte[] toRawBytes() {
		return null;
	}
	
	private String deviceInfoToString() {
		String result = "!!!";
		// TODO read bits
		return result;
	}
	
	private String voltageDegreeToString() {
		return String.format("%d%%", (int)(voltageDegree/6f*100));
	}
	
	private String GSMSignalStrenthToString() {
		return String.format("%d%%", (int)(GSMSignalStrenth/4f*100));
	}	
	public LinkedHashMap<String, String> getDataContent() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		if(deviceInfo != 0) result.put("Device Information", deviceInfoToString());
		result.put("Voltage Degree", voltageDegreeToString());
		result.put("GSM Signal Strength Degree", GSMSignalStrenthToString());
		return result;
	}	
}
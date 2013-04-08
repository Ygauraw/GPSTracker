package ru.cyfn.TrackerCommunication;

import java.util.*;

/*
 * StatusData
 * Device info - 1 byte
 * 		0 bit - reserved
 * 		1 bit - reserved
 * 		2 bit - reserved
 * 		3,4,5 bits - 011:Low-power alarm, 100:SOS
 * 		6 bit - 1:GPS has located, 0:GPS hasn't located
 * 		7 bit - reserved
 * Voltage degree - 1 byte
 * GSM signal strength degree - 1 byte
 */

class StatusData extends DataContent {
	private byte deviceInfo;
	private byte voltageDegree;
	private byte GSMSignalStrenth;
	
	public StatusData(byte[] rawData) {
		super(rawData);
		deviceInfo = rawData[0];
		voltageDegree = rawData[1];
		GSMSignalStrenth = rawData[2];
	}
	
	public byte[] toRawBytes() {
		return null;
	}
	
	private String deviceInfoToString() {
		String result = String.format("0x%X", deviceInfo);
		// TODO read bits
		// 		3,4,5 bits - 011:Low-power alarm, 100:SOS
		// 		6 bit - 1:GPS has located, 0:GPS hasn't located
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
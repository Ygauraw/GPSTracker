package ru.cyfn.TrackerCommunication;

import java.util.*;

/*
 * LoginData
 * Device ID - 8 chars
 * Type identity code - 2 chars
 */

class LoginData extends DataContent {
	private byte[] deviceID;
	private byte[] typeIdentityCode;
	
	public LoginData(byte[] rawData) {
		deviceID = Arrays.copyOfRange(rawData, 0, 8);
		typeIdentityCode = Arrays.copyOfRange(rawData, 8, 10);
	}
	
	public byte[] toRawBytes() {
		return null;
	}
	
	private String deviceIdToString() {
		String result = "";
		
		for (int i = 0; i < deviceID.length; i++) {
			result = result + String.format("%X", (int)deviceID[i]);
		}
		return result;
	}
	
	public LinkedHashMap<String, String> getDataContent() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("Device ID", deviceIdToString());
		result.put("Type identity code", String.format("0x%X,0x%X",(int)typeIdentityCode[0],(int)typeIdentityCode[1]));
		return result;
	}	
}
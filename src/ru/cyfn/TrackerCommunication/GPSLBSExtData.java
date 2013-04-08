package ru.cyfn.TrackerCommunication;

import java.util.*;

class GPSLBSExtData extends DataContent {

	private DataContent dateTime;
	private DataContent gpsData;
	private DataContent lbsData;
	
	public GPSLBSExtData(byte[] rawData) {
		int gpsDataLength = ((int)rawData[6] >>> 4) & 0x0F;
		dateTime = new DateTimeData(Arrays.copyOfRange(rawData, 0, 6));
		gpsData = new GPSData(Arrays.copyOfRange(rawData, 6, gpsDataLength + 6));
		lbsData = new LBSExtData(Arrays.copyOfRange(rawData, gpsDataLength + 6, rawData.length));
	}
	
	public byte[] toRawBytes() {
		// TODO Auto-generated method stub
		return null;
	}

	public LinkedHashMap<String, String> getDataContent() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.putAll(dateTime.getDataContent());
		result.putAll(gpsData.getDataContent());
		result.putAll(lbsData.getDataContent());
		return result;
	}

}

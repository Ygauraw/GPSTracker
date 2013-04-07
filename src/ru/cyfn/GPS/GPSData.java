package ru.cyfn.GPS;

import java.util.*;

/*
 * GPSData
 * 
 */

class GPSData extends DataContent {

	private int numbreOfSatellites;
	private int latitude;
	private int longitude;
	private int speed;				// in km/h
	private int course;
	
	public GPSData(byte[] rawData) {

		numbreOfSatellites = rawData[0] & 0xF;
		latitude = convertBytesToIntUnsigned(Arrays.copyOfRange(rawData, 1, 5)) / 30000 / 60;
		longitude = convertBytesToIntUnsigned(Arrays.copyOfRange(rawData, 5, 9)) / 30000 / 60;
		speed = ((int)rawData[9]) & 0xFF;
		//course = 
	}
	
	public byte[] toRawBytes() {
		return null;
	}
	
	public LinkedHashMap<String, String> getDataContent() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("No. of Satellites", new Integer(numbreOfSatellites).toString());
		result.put("Latitude", new Integer(latitude).toString());
		result.put("Longitude", new Integer(longitude).toString());
		result.put("Speed (km/h)", new Integer(speed).toString());
		
		return result;
	}	
}
package ru.cyfn.GPS;

import java.util.*;

/*
 * LBSExtData
 * DateTime - 6 bytes
 * MCC - 2 bytes
 * MNC - 1 byte
 * 
 * LAC - 2
 * CI - 3
 * RSSI - 1
 */

class LBSExtData extends GPSDataContent {
	private byte[] dateTime;
	private int mcc;
	private int mnc;
	private CellInfo mainCell;
	private CellInfo[] neighborCells = new CellInfo[6];
	
	public LBSExtData(byte[] rawData) {
		dateTime = Arrays.copyOfRange(rawData, 0, 6);
		mcc = convertBytesToIntUnsigned(Arrays.copyOfRange(rawData, 6, 8));
		mnc = convertBytesToIntUnsigned(Arrays.copyOfRange(rawData, 8, 9));
		mainCell = new CellInfo();
		mainCell.lac = convertBytesToIntUnsigned(Arrays.copyOfRange(rawData, 9, 11));
		mainCell.cellId = convertBytesToIntUnsigned(Arrays.copyOfRange(rawData, 11, 14));
		mainCell.receivedSignalStrengthIndicator = convertBytesToIntUnsigned(Arrays.copyOfRange(rawData, 14, 15));
		int offset = 15;
		for (int i = 0; i < 6; i++) {
			neighborCells[i] = new CellInfo();
			neighborCells[i].lac = convertBytesToIntUnsigned(Arrays.copyOfRange(rawData, offset, offset+2));
			neighborCells[i].cellId = convertBytesToIntUnsigned(Arrays.copyOfRange(rawData, offset+2,  offset+5));
			neighborCells[i].receivedSignalStrengthIndicator = convertBytesToIntUnsigned(Arrays.copyOfRange(rawData, offset+5, offset+6));
			offset += 6;
		}
	}
	
	private String dateTimeToString() {
		String result = "";
		int tmp = 0;
		for (int i = 0; i < dateTime.length; i++) {
			tmp += dateTime[i];
		}
		if(tmp>0)
			result = dateTime.toString();
		
		return result;
	}
	
	public byte[] toRawBytes() {
		return null;
	}
	
	public LinkedHashMap<String, String> getDataContent() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		if(!dateTimeToString().equals("")) result.put("Date & Time", dateTimeToString());
		result.put("MCC", new Integer(mcc).toString());
		result.put("MNC", new Integer(mnc).toString());
		result.put("Main LAC", new Integer(mainCell.lac).toString());
		result.put("Main CI", new Integer(mainCell.cellId).toString());
		result.put("Main RSSI", new Integer(mainCell.receivedSignalStrengthIndicator).toString());
		for (int i = 0; i < neighborCells.length; i++) {
			if (neighborCells[i].lac == 0) continue;
			result.put("LAC"+i, new Integer(neighborCells[i].lac).toString());
			result.put("CI"+i, new Integer(neighborCells[i].cellId).toString());
			result.put("RSSI"+i, new Integer(neighborCells[i].receivedSignalStrengthIndicator).toString());
		}
		
		return result;
	}	
	
	class CellInfo {
		int lac;
		int cellId;
		int receivedSignalStrengthIndicator;
	}
}
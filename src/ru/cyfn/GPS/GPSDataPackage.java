package ru.cyfn.GPS;

import java.util.*;

public class GPSDataPackage {
	
	public static final int LOGIN_PKG = 0x01;
	public static final int STATUS_PKG = 0x13;
	public static final Map<Integer, String> typeNames;
	static {
        Map<Integer, String> aMap = new HashMap<Integer, String>();
        aMap.put(LOGIN_PKG, "Login package");
        aMap.put(STATUS_PKG, "Status package");
        typeNames = Collections.unmodifiableMap(aMap);
    }
	
	private int packageType;
	private char[] serialNumber = new char[2];
	private boolean validCRC;
	private char[] rawContent;
	private GPSDataContent dataContent;
	
	public int getType() {
		return packageType;
	}
	public boolean isValid() {
		return validCRC;
	}
	public char[] getRawContent() {
		return Arrays.copyOf(rawContent, rawContent.length);
	}
	public LinkedHashMap<String, String> getDataContent() {
		return dataContent.getDataContent();
	}
	public GPSDataPackage(char[] data) {
		rawContent = Arrays.copyOf(data, data.length);
		init();
	}
	
	private GPSDataPackage(int type, char[] serialNum) {
		char[] result = new char[10];
		result[0] = 0x78;
		result[1] = 0x78;
		result[2] = 0x5;
		result[3] = (char)type;
		result[4] = serialNum[0];
		result[5] = serialNum[1];
		int crc = CRC.getCRC_ITU(Arrays.copyOfRange(result, 2, 6));
		result[6] = (char)((crc & 0xFF00)>>>8);
		result[7] = (char)(crc & 0xFF);
		result[8] = 0xD;
		result[9] = 0xA;
		rawContent = result;
		init();
	}
	
	private void init() {
		validCRC = isPacketValid(rawContent);
		packageType = rawContent[3];
		serialNumber[0] = rawContent[rawContent.length-6];
		serialNumber[1] = rawContent[rawContent.length-5];
		dataContent = constructDataContent(packageType, Arrays.copyOfRange(rawContent, 4, rawContent.length - 6));
	}
	
	private GPSDataContent constructDataContent(int type, char[] data) {
		GPSDataContent result = null;
		if(data.length == 0) return null;
			switch (type) {
			case LOGIN_PKG:
				result = new LoginData(data);
				break;
			}
		return result;
	}
	
	public GPSDataPackage createResponse() {
		return new GPSDataPackage(packageType, serialNumber);
	}
	
	public String toString() {
		String result = typeNames.get(this.packageType) + ", CRC check:" + (this.isValid() ? "PASS" : "FAIL");
		if(dataContent != null) {
			result = result + ", Data: ";
			for (Map.Entry<String, String> entry : dataContent.getDataContent().entrySet()) {
				result = result + entry.getKey() + ": " + entry.getValue() + ",";
			}
		}
		return result;
	}
	
	private boolean isPacketValid(char[] data) {
		int crc = CRC.getCRC_ITU(Arrays.copyOfRange(data, 2, data.length-4));
		if(data[data.length-3] == (crc & 0xFF) && data[data.length-4] == (crc>>>8))
			return true;
		return false;
	}
}
package ru.cyfn.GPS;

import java.util.*;

public class GPSDataPackage {
	
	public static final int LOGIN_PKG = 0x01;
	public static final int STATUS_PKG = 0x13;
	public static final int LBS_EXT_PKG = 0x18;
	public static final Map<Integer, String> typeNames;
	static {
        Map<Integer, String> aMap = new HashMap<Integer, String>();
        aMap.put(LOGIN_PKG, "Login Package");
        aMap.put(STATUS_PKG, "Status Package");
        aMap.put(LBS_EXT_PKG, "LBS Extend Information Package");
        aMap.put(0x10, "0x10");
        aMap.put(0x11, "0x11");
        aMap.put(0x12, "0x12");
        aMap.put(0x14, "0x14");
        aMap.put(0x15, "0x15");
        aMap.put(0x16, "0x16");
        aMap.put(0x17, "0x17");
        aMap.put(0x19, "0x19");
        aMap.put(0x1A, "0x1A");
        aMap.put(0x80, "0x80");
        aMap.put(0x81, "0x81");
        typeNames = Collections.unmodifiableMap(aMap);
    }
	
	private int packageType;
	private byte[] serialNumber = new byte[2];
	private boolean validCRC;
	private byte[] rawContent;
	private GPSDataContent dataContent;
	
	public int getType() {
		return packageType;
	}
	public boolean isValid() {
		return validCRC;
	}
	public byte[] getRawContent() {
		return Arrays.copyOf(rawContent, rawContent.length);
	}
	public LinkedHashMap<String, String> getDataContent() {
		return dataContent.getDataContent();
	}
	public GPSDataPackage(byte[] data) {
		rawContent = Arrays.copyOf(data, data.length);
		init();
	}
	
	private GPSDataPackage(int type, byte[] serialNum) {
		byte[] result = new byte[10];
		result[0] = 0x78;
		result[1] = 0x78;
		result[2] = 0x5;
		result[3] = (byte)type;
		result[4] = serialNum[0];
		result[5] = serialNum[1];
		int crc = CRC.getCRC_ITU(Arrays.copyOfRange(result, 2, 6));
		result[6] = (byte)((crc>>>8) & 0xFF);
		result[7] = (byte)(crc & 0xFF);
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
	
	private GPSDataContent constructDataContent(int type, byte[] data) {
		GPSDataContent result = null;
		if(data.length == 0) return null;
			switch (type) {
			case LOGIN_PKG:
				result = new LoginData(data);
				break;
			case STATUS_PKG:
				result = new StatusData(data);
				break;
			case LBS_EXT_PKG:
				result = new LBSExtData(data);
				break;
			default:
				result = new GenericData(data);
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
	
	private boolean isPacketValid(byte[] data) {
		int crc = CRC.getCRC_ITU(Arrays.copyOfRange(data, 2, data.length-4));
		if(data[data.length-3] == (byte)(crc & 0xFF) && data[data.length-4] == (byte)(crc>>>8))
			return true;
		return false;
	}
}
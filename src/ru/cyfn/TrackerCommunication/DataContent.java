package ru.cyfn.TrackerCommunication;

import java.util.*;

abstract class DataContent {
	public abstract byte[] toRawBytes();
	public abstract LinkedHashMap<String, String> getDataContent();
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
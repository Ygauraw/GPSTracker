package ru.cyfn.TrackerCommunication;

import java.util.*;

public interface DataPackage {
	public byte[] getRawContent();
	public LinkedHashMap<String, String> getDataContent();
	//public GPSDataPackage(byte[] data);
	public GPSDataPackage createResponse();
	public String toString();
}
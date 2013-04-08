package ru.cyfn.TrackerCommunication;


public class ProtocolRecognizer{
	// creates appropriate concrete subclass of DataPackage
	public GPSDataPackage createDataPackage(byte[] rawData) {
		return null;
	}
	
	/* testRawBytes
	 * 
	 * Tests raw data.
	 * Returns -1, 0 or 1
	 * -1: raw data doesn't represent protocol
	 * 0 : raw data can represent protocol
	 * 1 : raw data represents protocol
	 */
	public int testRawBytes(byte[] rawData) {
		return 0;
	}
	
	public String getProtocolName() {
		return "Generic Protocol";
	}
}

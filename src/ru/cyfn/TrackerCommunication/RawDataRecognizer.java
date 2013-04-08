package ru.cyfn.TrackerCommunication;

import java.security.acl.LastOwnerException;
import java.util.Collections;
import java.util.TreeSet;

public class RawDataRecognizer {

	private TreeSet<ProtocolRecognizer> registredRecognizers;
	private ProtocolRecognizer recognizedType;
	
	public RawDataRecognizer(TreeSet<ProtocolRecognizer> recognizers) {
		this.registredRecognizers = recognizers;
	}

	// returns true when data recognition competes
	public boolean recognize(byte[] rawData) {
		boolean recognized = false;
		
		for (ProtocolRecognizer recognizer : registredRecognizers) {
			int testResult = recognizer.testRawBytes(rawData);
			if(testResult == -1) {
				registredRecognizers.remove(recognizer);
				continue;
			} else if (testResult == 1) {
				recognizedType = recognizer;
				break;
			}
		}
		if(registredRecognizers.size() == 1) recognizedType = registredRecognizers.last();
		if(recognizedType != null) recognized = true;
		
		return recognized;
	}
	
	public GPSDataPackage createDataPackage(byte[] rawData) {
		return null;
	}
}

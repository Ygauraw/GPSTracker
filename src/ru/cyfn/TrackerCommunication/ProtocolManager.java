package ru.cyfn.TrackerCommunication;

import java.util.*;

import ru.cyfn.TrackerCommunication.GK301.GK301ProtocolRecognizer;

public class ProtocolManager {

	private TreeSet<Class<? extends ProtocolRecognizer>> registredRecognizers;
	
	public ProtocolManager() {
		registerKnownProtocolRecognizers();
	}
	
	public  void registerProtocolRecognizer(Class<? extends ProtocolRecognizer> recognizer) {
		registredRecognizers.add(recognizer);
	}
	
	public RawDataRecognizer getRawDataRecognizer() {
		TreeSet<ProtocolRecognizer> recognizers = new TreeSet<ProtocolRecognizer>();
		for (Class<? extends ProtocolRecognizer> recognizer : registredRecognizers) {
			try {
				recognizers.add(recognizer.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return new RawDataRecognizer(recognizers);
	}
	
	private void registerKnownProtocolRecognizers() {
		registerProtocolRecognizer(GK301ProtocolRecognizer.class);
		
	}
	
	public DataPackage createDataPackage(char[] data) {
		return null;
	}
}

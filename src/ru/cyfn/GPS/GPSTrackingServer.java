package ru.cyfn.GPS;

import java.io.*;
import java.net.*;
import java.util.*;

public class GPSTrackingServer {
	ArrayList<Socket> clientSockets;

	public void go() {
		clientSockets = new ArrayList<Socket>();
		try {
			ServerSocket serverSock = new ServerSocket(5000);
			System.out.println("GPSTrackingServer is waiting for clients");
			while (true) {
				Socket clientSocket = serverSock.accept();
				clientSockets.add(clientSocket);
				Thread t = new Thread(new ClientHandler(clientSocket));
				t.start();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

class ClientHandler implements Runnable {
	private GPSDataReader in;
	private Socket clientSocket;
	
	public ClientHandler(Socket socket) {
		try {
			clientSocket = socket;
			in = new GPSDataReader(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())));
			System.out.println("Connection from IP: " + clientSocket.getInetAddress().getHostAddress());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void run() {
		GPSDataPackage packet;
		try {
			while (!clientSocket.isClosed() && (packet = in.readGPSData()) != null) {
				System.out.print("In:  ");
				printArray(packet.getRawContent());
				respond(packet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Connection closed");
	}
	
	private void respond(GPSDataPackage packet) {
		OutputStreamWriter out;
		if(packet.getType() == GPSDataPackage.LOGIN_PKG || packet.getType() == GPSDataPackage.STATUS_PKG) {
			GPSDataPackage response = packet.createResponse();
			try {
				out = new OutputStreamWriter(clientSocket.getOutputStream());
				out.write(response.getRawContent());
				out.flush();
				System.out.print("Out: ");
				printArray(response.getRawContent());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	void printArray(char[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(String.format("0x%X,", (int) array[i]));
		}
		System.out.println("");
	}
}

class GPSDataPackage {
	
	public static final int LOGIN_PKG = 0x01;
	public static final int STATUS_PKG = 0x13;
	public static final List<Integer> validTypes = 
		    Collections.unmodifiableList(Arrays.asList(LOGIN_PKG, STATUS_PKG));
	
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
		int crc = CRC.getCRC(Arrays.copyOfRange(result, 2, 6));
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
	
	private boolean isPacketValid(char[] data) {
		int crc = CRC.getCRC(Arrays.copyOfRange(data, 2, data.length-4));
		if(data[data.length-3] == (crc & 0xFF) && data[data.length-4] == (crc>>>8))
			return true;
		return false;
	}
}

interface GPSDataContent {
	public char[] toRawChars();
	public LinkedHashMap<String, String> getDataContent();
}

/*
 * LoginData
 * Device ID - 8 chars
 * Type identity code - 2 chars
 */

class LoginData implements GPSDataContent {
	private char[] deviceID;
	private char[] typeIdentityCode;
	
	public LoginData(char[] rawData) {
		deviceID = Arrays.copyOfRange(rawData, 0, 8);
		typeIdentityCode = Arrays.copyOfRange(rawData, 8, 10);
	}
	
	public char[] toRawChars() {
		return null;
	}
	
	private String deviceIdToString() {
		String result = "";
		
		for (int i = 0; i < deviceID.length; i++) {
			result = result + String.format("%x", deviceID[i]);
		}
		return result.substring(1);
	}
	
	public LinkedHashMap<String, String> getDataContent() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("Device ID", deviceIdToString());
		result.put("Type identity code", String.format("0x%X,0x%X",typeIdentityCode[0],typeIdentityCode[1]));
		return result;
	}	
}
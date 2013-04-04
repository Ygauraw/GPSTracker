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
	char[] LOGIN_RESPONSE = {0x78, 0x78, 0x5, 0x1, 0x9, 0xAE, 0x36, 0xF, 0xD, 0xA};

	
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
				System.out.print("In: ");
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
		if(packet.getType() == 0x01) {
			try {
				out = new OutputStreamWriter(clientSocket.getOutputStream());
				LOGIN_RESPONSE[4] = packet.getSerialNumber()[0];
				LOGIN_RESPONSE[5] = packet.getSerialNumber()[1];
				int crc = CRC.getCRC(Arrays.copyOfRange(LOGIN_RESPONSE, 2, 6));
				LOGIN_RESPONSE[6] = (char)((crc & 0xFF00)>>>8);
				LOGIN_RESPONSE[7] = (char)(crc & 0xFF);
				out.write(LOGIN_RESPONSE);
				out.flush();
				System.out.print("Out: ");
				printArray(LOGIN_RESPONSE);
				//out.close();
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

enum GPSDataPackageType {
	LOGIN(0x01);
	
	private int typeCode;
	GPSDataPackageType(int type) {
		typeCode = type;
	}
	public int getTypeCode() {
		return typeCode;
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
	
	// !!! delete this method after implementing createResponse()
	public char[] getSerialNumber() {
		return Arrays.copyOf(serialNumber, serialNumber.length);
	}
	
	public int getType() {
		return packageType;
	}
	
	public boolean isValid() {
		return validCRC;
	}
	
	public char[] getRawContent() {
		return Arrays.copyOf(rawContent, rawContent.length);
	}
	
	public GPSDataPackage(char[] data) {
		rawContent = Arrays.copyOf(data, data.length);
		validCRC = isPacketValid(rawContent);
		if(validCRC) {
			packageType = rawContent[3];
			serialNumber[0] = rawContent[rawContent.length-6];
			serialNumber[1] = rawContent[rawContent.length-5];
			dataContent = constructDataContent(packageType, Arrays.copyOfRange(rawContent, 4, rawContent.length - 6));
		}
	}
	
	private GPSDataContent constructDataContent(int type, char[] data) {
		GPSDataContent result = null;
		
		switch (type) {
		case LOGIN_PKG:
			result = new LoginData(data);
			break;
		}
		return result;
	}
	
	public GPSDataPackage createResponse() {
		// construct and return appropriate response for current package type
		return null;
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
	public String toString();
}

class LoginData implements GPSDataContent {
	private char[] deviceID;
	private char[] typeIdentityCode;
	
	public LoginData(char[] rawData) {
		
	}
	
	public char[] toRawChars() {
		return null;
	}
	
	public String toString() {
		return null;
	}	
}
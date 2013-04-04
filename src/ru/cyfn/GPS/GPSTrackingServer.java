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
			in = new GPSDataReader(new BufferedInputStream(clientSocket.getInputStream()));
			System.out.println("Connection from IP: " + clientSocket.getInetAddress().getHostAddress());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void run() {
		GPSDataPackage packet;
		try {
			while (!clientSocket.isClosed() && (packet = in.readGPSData()) != null) {
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
	
	int packageType;
	char[] serialNumber = new char[2];
	int crc;
	private boolean validPacket;
	private char[] rawContent;
	
	public char[] getSerialNumber() {
		return Arrays.copyOf(serialNumber, serialNumber.length);
	}
	
	public int getType() {
		return packageType;
	}
	
	public boolean isValid() {
		return validPacket;
	}
	
	public char[] getRawContent() {
		return Arrays.copyOf(rawContent, rawContent.length);
	}
	
	public GPSDataPackage(char[] data) {
		rawContent = data;
		validPacket = isPacketValid(rawContent);
		if(validPacket) {
			packageType = rawContent[3];
			serialNumber[0] = rawContent[rawContent.length-6];
			serialNumber[1] = rawContent[rawContent.length-5];
		}
	}
	
	private boolean isPacketValid(char[] data) {
		if(data.length <= 10) return false;
		if(data[0] != 0x78) return false;
		if(data[1] != 0x78) return false;
		int pkgLength = data[2];
		if(data.length != pkgLength+5) return false;
		if(data[data.length-1] != 0x0A) return false;
		if(data[data.length-2] != 0x0D) return false;
		System.out.println(String.format("0x%X", CRC.getCRC(Arrays.copyOfRange(data, 2, data.length-4))));
		return true;
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
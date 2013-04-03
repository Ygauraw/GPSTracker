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
	private BufferedReader in;
	private Socket clientSocket;
	char[] LOGIN_RESPONSE = {0x78, 0x78, 0x5, 0x1, 0x9, 0xAE, 0x36, 0xF, 0xD, 0xA};

	
	public ClientHandler(Socket socket) {
		try {
			clientSocket = socket;
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println("Connection from IP: " + clientSocket.getInetAddress().getHostAddress());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void run() {
		char[] message;
		while (!clientSocket.isClosed() && (message = readPacket()) != null) {
			printArray(message);
			GPSDataPackage packet = new GPSDataPackage(message);
			//System.out.println(packet.isValid());
			if(packet.getType() == 0x01) {
				OutputStreamWriter out;
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
		System.out.println("Connection closed");
	}
	
	private char[] readPacket() {
		char[] buffer = new char[255];
		char[] result = null;
		int index = 0;
		int message;
		try {
			while((message = in.read()) != -1) {
				buffer[index] = (char)message;
				if(message == 0x0A && index > 0 && buffer[index-1] == 0x0D) break;
				index++;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
			
		// Make a nice short array
		if(index>0) {
			result = new char[index];
			for (int i = 0; i < result.length; i++) {
				result[i] = buffer[i];
			}
		}
		return result;
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

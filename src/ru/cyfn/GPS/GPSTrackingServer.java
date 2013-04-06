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
				System.out.print("In:  ");
				//printArray(packet.getRawContent());
				System.out.println(packet);
				respond(packet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Connection closed");
	}
	
	private void respond(GPSDataPackage packet) {
		BufferedOutputStream out;
		// responds to all packages
		if(true /*packet.getType() == GPSDataPackage.LOGIN_PKG || packet.getType() == GPSDataPackage.STATUS_PKG*/) {
			GPSDataPackage response = packet.createResponse();
			try {
				out = new BufferedOutputStream(clientSocket.getOutputStream());
				out.write(response.getRawContent());
				out.flush();
				//System.out.print("Out: ");
				//printArray(response.getRawContent());
				//System.out.println(response);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	void printArray(byte[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.print(String.format("0x%X,", array[i]));
		}
		System.out.println("");
	}
}
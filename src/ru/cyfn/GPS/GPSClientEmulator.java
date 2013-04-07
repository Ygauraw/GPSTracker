package ru.cyfn.GPS;

import java.io.*;
import java.net.*;

public class GPSClientEmulator {

	static public byte[] LOGIN_PKG = { 
		0x78,0x78,70,0x1E,0xD,0x4,0x7,0xA,0x11,0x1C,(byte)0xC0,0x5,(byte)0xFD,0x2,(byte)0xA9,0x4,0xC,0x34,(byte)0x82,0x0,0x14,0x0,0x0,(byte)0xFA,0x1,0x1,
		(byte)0x96,0x0,(byte)0xD4,0x3F,0x53,0x1,(byte)0x96,0x0,(byte)0xD1,(byte)0x89,0x4D,0x1,(byte)0x96,0x0,(byte)0xD4,0x3D,0x4E,0x1,(byte)0x96, //42
		0x0,(byte)0xD1,(byte)0x8A,0x4F,0x1,(byte)0x96,0x0,(byte)0xD6,0x13,0x55,0x1,(byte)0x95,0x0,0xB,(byte)0xC3,0x58,0x1,(byte)0x96,0x0,(byte)0xCD,
		(byte)0xD3,0x58,(byte)0xFF,0x0,0x2,0x0,0x0,0xD,0xA,
		};

	BufferedInputStream reader;
	BufferedOutputStream writer;
	Socket sock;
	
	public static void main(String[] args) {
		new GPSClientEmulator().go();
	}

	public void go() {

		setUpNetworking();

		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
		sendMessage(LOGIN_PKG);

	}

	private void setUpNetworking() {
		try {
			//sock = new Socket("183.60.142.137", 8827);					// coomix free GPS tracking service
			sock = new Socket("127.0.0.1", 5000);						// local server
			reader = new BufferedInputStream(sock.getInputStream());
			writer = new BufferedOutputStream(sock.getOutputStream());
			System.out.println("networking established");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void sendMessage(byte[] message) {
		try {
			writer.write(message);
			writer.flush();
			System.out.println("Message sent");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public class IncomingReader implements Runnable {
		public void run() {
			int message;
			try {
				while ((message = reader.read()) != -1) {
					System.out.print(String.format("0x%X, ", message));
				}
				reader.close();
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
		}

		void printArray(byte[] array) {
			for (int i = 0; i < array.length; i++) {
				System.out.print(String.format("0x%X,", array[i]));
			}
			System.out.println("");
		}
	}

}
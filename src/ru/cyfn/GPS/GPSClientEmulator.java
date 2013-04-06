package ru.cyfn.GPS;

import java.io.*;
import java.net.*;

public class GPSClientEmulator {

	static public byte[] LOGIN_PKG = { 
		0x78,0x78,0x3B,0x18,0x0,0x0,0x0,0x0,0x0,0x0,0x0,(byte)0xFA,0x1,0x1,(byte)0x96,0x0,(byte)0xD4,0x3F,0x4F,
		0x1,(byte)0x96,0x0,(byte)0xD4,0x3C,0x43,0x1,(byte)0x96,0x0,(byte)0xD4,0x3D,0x52,0x1,(byte)0x96,0x0,(byte)0xD1,(byte)0x8A,0x53,
		0x1,(byte)0x96,0x0,(byte)0xD1,(byte)0x89,0x55,0x1,(byte)0x96,0x0,0x44,(byte)0xC6,0x56,0x1,(byte)0x96,0x0,(byte)0xD1,(byte)0x8C,0x59,
		(byte)0xFF,0x0,0x2,0xE,0xF,(byte)0xB5,(byte)0xC7,0xD,0xA,
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
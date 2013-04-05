package ru.cyfn.GPS;

import java.io.*;
import java.net.*;

public class GPSClientEmulator {

	static public char[] LOGIN_PKG = { 
		0x78,0x78,0xA,0x13,0x0,0x5,0x3,0x0,0x2,0x8,0xF6,0xD5,0xFC,0xD,0xA, 
		};

	BufferedReader reader;
	OutputStreamWriter writer;
	Socket sock;
	
	public static void main(String[] args) {
		new GPSClientEmulator().go();
	}

	public void go() {

		setUpNetworking();

		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sendMessage(LOGIN_PKG);

	}

	private void setUpNetworking() {
		try {
			//sock = new Socket("183.60.142.137", 8827);					// coomix free GPS tracking service
			sock = new Socket("127.0.0.1", 5000);						// local server
			//InputStreamReader streamReader = new InputStreamReader(
			//		sock.getInputStream());
			//reader = new BufferedReader(streamReader);
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			writer = new OutputStreamWriter(sock.getOutputStream());
			System.out.println("networking established");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void sendMessage(char[] message) {
		try {
			//writer.print(message);
			//writer.flush();
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

		void printArray(char[] array) {
			for (int i = 0; i < array.length; i++) {
				System.out.print(String.format("0x%X,", (int) array[i]));
			}
			System.out.println("");
		}
	}

}
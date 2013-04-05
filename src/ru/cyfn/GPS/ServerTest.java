package ru.cyfn.GPS;

public class ServerTest {

	public static void main(String[] args) {
		//CRCTest();
		new GPSTrackingServer().go();
	}
	
	private static void CRCTest() {
		  byte[] array = { 
				  			0x5,0x13,0xE,0xE
		  				 };
		System.out.println(String.format("Calculated CRC value: 0x%X", CRC.getCRC_ITU(array)));
	}
}


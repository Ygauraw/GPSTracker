package ru.cyfn.GPS;

import java.util.*;

public class ServerTest {

	public static void main(String[] args) {
		//CRCTest();
		new GPSTrackingServer().go();
	}
	
	private static void CRCTest() {
		
		System.out.println(String.format("%tY-%<tm-%<td %<tH:%<tM:%<tS",new Date()));
		  byte[] array = { 
				  			0x5,0x13,0xE,0xE
		  				 };
		System.out.println(String.format("Calculated CRC value: 0x%X", CRC.getCRC_ITU(array)));
	}
}


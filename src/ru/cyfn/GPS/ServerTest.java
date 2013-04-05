package ru.cyfn.GPS;

public class ServerTest {

	public static void main(String[] args) {
		//CRCTest();
		new GPSTrackingServer().go();
		

	}
	
	private static void CRCTest() {
		  char[] a ={ 
				  0xA,0x13,0x0,0x5,0x3,0x0,0x2,0x8,0xF6
				  };
		System.out.println(String.format("Calculated CRC value: 0x%X", CRC.getCRC(a)));
		//System.out.println("Control CRC value: 0xF794");
	}
}


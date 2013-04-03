package ru.cyfn.GPS;

public class ServerTest {

	public static void main(String[] args) {
		new GPSTrackingServer().go();
		//CRCTest();

	}
	
	private static void CRCTest() {
		  char[] a ={ 
				  0x5,0x1,0x3,0x45
				  };
		System.out.println(String.format("Calculated CRC value: 0x%X", CRC.getCRC(a)));
		System.out.println("Control CRC value: 0xF794");
	}
}
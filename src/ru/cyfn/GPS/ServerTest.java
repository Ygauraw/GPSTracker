package ru.cyfn.GPS;

public class ServerTest {

	public static void main(String[] args) {
		CRCTest();
		new GPSTrackingServer().go();
		

	}
	
	private static void CRCTest() {
		  char[] a ={ 
				  0x11,0x1,0x8,0x64,0x71,0x70,0x3,0x64,0x79,0x59,0x10,0x12,0x19,0x2,0x8,0xEF
				  };
		System.out.println(String.format("Calculated CRC value: 0x%X", CRC.getCRC(a)));
		//System.out.println("Control CRC value: 0xF794");
	}
}


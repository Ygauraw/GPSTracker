package ru.cyfn.GPS;

import java.io.*;
import java.util.Arrays;

public class GPSDataReader extends FilterInputStream {

	private static int BUFFER_SIZE = 255;
	char[] buffer;
	int index;
	
	protected GPSDataReader(InputStream in) {
		super(in);
	}

	public GPSDataPackage readGPSData() throws IOException {
		
		buffer = new char[BUFFER_SIZE];
		int data;
		index = 0;
		int start = -1;
		int type = 0;
		int end = BUFFER_SIZE;
		
		
		while((data = in.read()) != -1) {
			buffer[index] = (char)data;
			if(start == -1 && hasValidStartBits()) {			// if start bits found set start and end of a pkg
				start = index - 3;
				end = start + buffer[index - 1] + 5;
				type = buffer[index - 2];
			}
			if(index == end-1) {									// if end reached
				if(hasValidStopBits()) {							// and it contains valid stop bits
					return createGPSDataPackage(type,Arrays.copyOfRange(buffer, start, end)); // create an appropriate pkg
				} else {										// else reset start and end positions
					start = -1;
					end = BUFFER_SIZE;
					type = 0;
				}
			}
			index++;
		}
		
		return null;
	}

	private GPSDataPackage createGPSDataPackage(int type, char[] data) {
		// TODO Create appropriate GPSDataPackage
		return new GPSDataPackage(data);
	}

	private boolean hasValidStopBits() {
		// TODO Check last 2 buffer elements for 0xD,0xA
		if(index>0) {
			if(buffer[index] == 0xA && buffer[index-1] == 0xD)
				return true;
		}
		return false;
	}

	private boolean hasValidStartBits() {
		// TODO Check last 4 buffer elements for 78,78,??,valid type code
		if(index>2) {
			if(buffer[index-3] == 0x78 && buffer[index-2] == 0x78)
				return true;
		}
		return false;
	}


}

package ru.cyfn.GPS;

import java.io.*;
import java.util.*;

/*
 * This reader assumes all data packages starts with
 * 0x78,0x78 following by "data length" byte.
 * Total package length is ("data length" + 5)
 * Package terminates with 0xD,0xA sequence.
 */

public class GPSDataReader extends FilterInputStream {

	private static int BUFFER_SIZE = 255;
	byte[] buffer;
	int index;
	
	protected GPSDataReader(InputStream in) {
		super(in);
	}

	public GPSDataPackage readGPSData() throws IOException {
		
		buffer = new byte[BUFFER_SIZE];
		int data;
		index = 0;
		int start = -1;
		int end = BUFFER_SIZE;
		
		
		while((data = in.read()) != -1) {
			buffer[index] = (byte)(data & 0xFF);
			//System.out.print(String.format("%X.", data));
			if(start == -1 && hasValidStartBits()) {			// if start bits found set start and end of a pkg
				start = index - 2;
				end = start + buffer[index] + 5;
			}
			if(index == end-1) {									// if end reached
				if(hasValidStopBits()) {							// and it contains valid stop bits
					return createGPSDataPackage(Arrays.copyOfRange(buffer, start, end)); // create an appropriate pkg
				} else {										// else reset start and end positions
					start = -1;
					end = BUFFER_SIZE;
				}
			}
			index++;
		}
		return null;
	}

	private GPSDataPackage createGPSDataPackage(byte[] data) {
		return new GPSDataPackage(data);
	}

	private boolean hasValidStopBits() {
		if(index>0) {
			if(buffer[index] == 0xA && buffer[index-1] == 0xD)
				return true;
		}
		return false;
	}

	private boolean hasValidStartBits() {
		if(index>1) {
			if(buffer[index-2] == 0x78 && buffer[index-1] == 0x78)
				return true;
		}
		return false;
	}
}

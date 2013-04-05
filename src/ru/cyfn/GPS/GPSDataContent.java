package ru.cyfn.GPS;

import java.util.*;

interface GPSDataContent {
	public byte[] toRawBytes();
	public LinkedHashMap<String, String> getDataContent();
}
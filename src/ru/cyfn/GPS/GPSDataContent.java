package ru.cyfn.GPS;

import java.util.*;

interface GPSDataContent {
	public char[] toRawChars();
	public LinkedHashMap<String, String> getDataContent();
}
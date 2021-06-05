package com.wendelnunes.assembleia.utils;

public class StringUtil {
	public static String removeMask(String value) {
		return value.replaceAll("[^\\d ]", "");
	}
}
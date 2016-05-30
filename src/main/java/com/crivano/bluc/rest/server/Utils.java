package com.crivano.bluc.rest.server;

import java.util.logging.Logger;

public class Utils {
	private static BlucUtil blucutil = new BlucUtil();

	private static final Logger log = Logger.getLogger(Utils.class.getName());

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static BlucUtil getBlucutil() {
		return blucutil;
	}

	public static void setBlucutil(BlucUtil blucutil) {
		Utils.blucutil = blucutil;
	}
}

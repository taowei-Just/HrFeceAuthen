package com.test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	public static String md5(String source) {
		try {
			MessageDigest e = MessageDigest.getInstance("MD5");
			byte[] array = e.digest(source.getBytes());
			StringBuilder builder = new StringBuilder();
			byte[] var4 = array;
			int var5 = array.length;

			for (int var6 = 0; var6 < var5; ++var6) {
				byte b = var4[var6];
				String hexString = Integer.toHexString(255 & b);
				if (hexString.length() < 2) {
					builder.append("0");
				}

				builder.append(hexString);
			}

			return builder.toString();
		} catch (NoSuchAlgorithmException var9) {
			var9.printStackTrace();
			return "";
		}
	}
}

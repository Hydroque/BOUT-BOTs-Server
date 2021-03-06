package shared;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

	
	public static String isoString(byte[] bytes) {
		try {
			return new String(bytes, "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	public static String sha512Digest(String text, String salt) {
		try {
			MessageDigest md = null;
			byte[] encryptMsg = null;
			try {
				md = MessageDigest.getInstance("SHA-512");
				md.update(salt.getBytes("ISO8859-1"));
				encryptMsg = md.digest(text.getBytes("ISO8859-1"));
			} catch (NoSuchAlgorithmException e) {
			}
			final StringBuffer strBuf = new StringBuffer();
			for (int i=0; i<encryptMsg.length; i++)
				strBuf.append(Integer.toString((encryptMsg[i] & 0xFF) + 0x100, 16).substring(1));
			return strBuf.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getbyteiso(int var, int num) {
		try {
			if (num == 2) {
				final int b1 = var & 0xff;
				final int b2 = (var >> 8) & 0xff;
				final byte[] varbyte = { (byte) b1, (byte) b2 };
				return Util.isoString(varbyte);
			} else if (num == 4) {
				final int b1 = var & 0xff;
				final int b2 = (var >> 8) & 0xff;
				final int b3 = (var >> 16) & 0xff;
				final int b4 = (var >> 24) & 0xff;
				final byte[] varbyte = { (byte) b1, (byte) b2, (byte) b3, (byte) b4 };
				return Util.isoString(varbyte);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int bytetoint(String thestring, int bytec) {
		try {
			String hex_data_s = "";
			for (int i = bytec - 1; i >= 0; i--) {
				int data = thestring.getBytes("ISO8859-1")[i];
				if (data < 0)
					data += 256;
				final String hex_data = Integer.toHexString(data);
				if (hex_data.length() == 1)
					hex_data_s += "0" + hex_data;
				else
					hex_data_s += hex_data;
			}
			return Integer.parseInt(hex_data_s, 16);
		} catch (Exception e) {
		}
		return 0;
	}
	
	public static String getbyte(int var, int num) {
		final StringBuffer tstring = new StringBuffer();
		if (num == 2) {
			final int b1 = var & 0xff;
			final int b2 = (var >> 8) & 0xff;
			tstring.appendCodePoint(b1);
			tstring.appendCodePoint(b2);
			return tstring.toString();
		} else if (num == 4) {
			final int b1 = var & 0xff;
			final int b2 = (var >> 8) & 0xff;
			final int b3 = (var >> 16) & 0xff;
			final int b4 = (var >> 24) & 0xff;
			tstring.appendCodePoint(b1);
			tstring.appendCodePoint(b2);
			tstring.appendCodePoint(b3);
			tstring.appendCodePoint(b4);
			return tstring.toString();
		}
		return null;
	}
	
	public static int getcmd(String packet) {
		try {
			String hex_data_s = "";
			for (int i = 0; i < 2; i++) {
				int data = packet.getBytes("ISO8859-1")[i];
				if (data < 0)
					data += 256;
				final String hex_data = Integer.toHexString(data);
				if (hex_data.length() == 1)
					hex_data_s += "0" + hex_data;
				else
					hex_data_s += hex_data;
			}
			return Integer.parseInt(hex_data_s, 16);
		} catch (Exception e) {
		}
		return 0;
	}
	
	public static int compareChat(String chatpack, String rlcharname, boolean whisper, boolean isgm) {
		try {
			final byte[] stringbyte = chatpack.getBytes("ISO8859-1");
			if (whisper == false)
				if (stringbyte[4] != 0x00 || stringbyte[6] != 0x5B && !isgm)
					return -1;
			int a = 0;
			while (stringbyte[a] != 0x5B)
				a++;
			
			int b = a;
			final int chstart = b + 1;
			int c = 0;
			while (stringbyte[b] != 0x5D) {
				if (c > 16)
					return -1;
				b++;
				c++;
			}
			
			final int chende = b;
			final String chname = chatpack.substring(chstart, chende);
			if (chname.equals(rlcharname))
				return a;
			else
				return -1;
		} catch (Exception e) {
		}
		return 0;
	}
	
	public static String removenullbyte(String thestring) {
		try {
			final byte[] stringbyte = thestring.getBytes("ISO8859-1");
			int a = 0;
			while (stringbyte[a] != 0x00)
				a++;
			return thestring.substring(0, a);
		} catch (Exception e) {

		}
		return null;
	}
	
}

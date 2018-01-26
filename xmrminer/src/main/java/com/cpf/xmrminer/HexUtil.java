package com.cpf.xmrminer;

/**
 * Created by cpf on 2018/1/17.
 */

public class HexUtil {

    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static int fromHexChar(char paramChar) {
        if ((paramChar >= '0') && (paramChar <= '9')) {
            paramChar -= 48;
        }
        if ((paramChar >= 'A') && (paramChar <= 'F')) {
            paramChar = (char) (paramChar - 'A' + 10);
        } else {
            paramChar = (char) (paramChar - 'a' + 10);
        }
        return paramChar;
    }

    public static byte[] fromHexString(String paramString) {
        if ((paramString.length() & 0x1) != 0) {
            throw new RuntimeException("Invalid hex string");
        }
        byte[] arrayOfByte = new byte[paramString.length() / 2];
        for (int i = 0; i < paramString.length() / 2; i++) {
            arrayOfByte[i] = ((byte) (fromHexChar(paramString.charAt(i * 2)) << 4 | fromHexChar(paramString.charAt(i * 2 + 1))));
        }
        return arrayOfByte;
    }

    public static String toHexString(byte[] paramArrayOfByte) {
        char[] arrayOfChar = new char[paramArrayOfByte.length * 2];
        for (int i = 0; i < paramArrayOfByte.length; i++) {
            int j = paramArrayOfByte[i] & 0xFF;
            arrayOfChar[(i * 2)] = hexArray[(j >>> 4)];
            arrayOfChar[(i * 2 + 1)] = hexArray[(j & 0xF)];
        }
        return new String(arrayOfChar);
    }

    /**
     * Transform a byte array into a it's hexadecimal representation
     */
    public static String hexlify(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        String ret = new String(hexChars);
        return ret;
    }

    /**
     * Transform a string of hexadecimal chars into a byte array
     */
    public static byte[] unhexlify(String argbuf) {
        int arglen = argbuf.length();
        if (arglen % 2 != 0)
            throw new RuntimeException("Odd-length string");

        byte[] retbuf = new byte[arglen / 2];

        for (int i = 0; i < arglen; i += 2) {
            int top = Character.digit(argbuf.charAt(i), 16);
            int bot = Character.digit(argbuf.charAt(i + 1), 16);
            if (top == -1 || bot == -1)
                throw new RuntimeException("Non-hexadecimal digit found");
            retbuf[i / 2] = (byte) ((top << 4) + bot);
        }
        return retbuf;
    }
}

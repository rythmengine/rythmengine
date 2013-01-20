package com.greenlaw110.rythm.security;

/**
 * Source code come from http://examples.oreilly.com/9781565924024/files/oreilly/jonathan/util/Base64.java
 */
public class Base64 {

    public static String encode(byte[] raw) {
      StringBuffer encoded = new StringBuffer();
      for (int i = 0; i < raw.length; i += 3) {
        encoded.append(encodeBlock(raw, i));
      }
      return encoded.toString();
    }
    
    protected static char[] encodeBlock(byte[] raw, int offset) {
      int block = 0;
      int slack = raw.length - offset - 1;
      int end = (slack >= 2) ? 2 : slack;
      for (int i = 0; i <= end; i++) {
        byte b = raw[offset + i];
        int neuter = (b < 0) ? b + 256 : b;
        block += neuter << (8 * (2 - i));
      }
      char[] base64 = new char[4];
      for (int i = 0; i < 4; i++) {
        int sixbit = (block >>> (6 * (3 - i))) & 0x3f;
        base64[i] = getChar(sixbit);
      }
      if (slack < 1) base64[2] = '=';
      if (slack < 2) base64[3] = '=';
      return base64;
    }

    protected static char getChar(int sixBit) {
       if (sixBit >= 0 && sixBit <= 25)
         return (char)('A' + sixBit);
       if (sixBit >= 26 && sixBit <= 51)
         return (char)('a' + (sixBit - 26));
       if (sixBit >= 52 && sixBit <= 61)
         return (char)('0' + (sixBit - 52));
       if (sixBit == 62) return '+';
       if (sixBit == 63) return '/';
       return '?';
     }
}

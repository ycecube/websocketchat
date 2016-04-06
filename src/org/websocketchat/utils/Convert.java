package org.websocketchat.utils;

/**
 *
 * @author
 * yce
 */
public class Convert {
  /**
   * Convert bytes to integer.
   * 
   * @param b array of bytes
   * @return integer
   */
  public static int byteArrayToInt(byte[] b) {
    int value = 0;
    
    for (int i = 0; i < b.length; i++) {
      int shift = (b.length - 1 - i) * 8;
      value += (b[i] & 0x000000FF) << shift;
    }
    
    return value;
  }
}

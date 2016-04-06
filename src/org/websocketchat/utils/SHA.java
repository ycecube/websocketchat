/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.websocketchat.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA encoders.
 */
public class SHA {
  /**
   * Convert string to SHA1
   * 
   * @param input
   * @return SHA1 encoded bytes
   * @throws NoSuchAlgorithmException 
   */
  public static byte[] encodeToSHA1(String input) throws NoSuchAlgorithmException {
    MessageDigest mDigest = MessageDigest.getInstance("SHA1");
    byte[] result = mDigest.digest(input.getBytes());

    return result;
  }
}

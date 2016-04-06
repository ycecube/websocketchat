/*
 * Main class for WebSocketChat.
 */
package org.websocketchat;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.websocketchat.net.WebSocketServer;

/**
 *
 * @author Tamás Nagy
 * @author ycecube@gmail.com
 */
public class WebSocketChat {
  public static void main(String[] args) {
    int port;
    
    // Set port if it is not set.
    if (args.length > 0) {
      port = Integer.parseInt(args[0]);
    }
    else {
      port = 5256;
    }
    
    try {
      WebSocketServer ws = new WebSocketServer(port);
    }
    catch (IOException | NoSuchAlgorithmException exception) {
      System.out.println(exception.getMessage());
    }
  }
}

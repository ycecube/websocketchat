package org.websocketchat.net;

import org.apache.commons.codec.binary.Base64;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;
import org.apache.commons.codec.binary.StringUtils;
import org.websocketchat.utils.SHA;

/**
 * @author Tamás Nagy
 */
public class WebSocketServer {
  private ServerSocket socket = null;
  
  private static String magicString = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
  
  public WebSocketServer(int port) throws IOException, NoSuchAlgorithmException {
    this.socket = new ServerSocket(port);
    
    this.init();
  }
  
  /**
   * Initialize client manager thread.
   * 
   * @throws IOException
   * @throws NoSuchAlgorithmException 
   */
  private void init() throws IOException, NoSuchAlgorithmException {
    // Start client manager thread
    Thread th = new Thread(new ClientManager(socket), "ClientManager");
    th.start();
  }
  
  /**
   * WebSocket handshake.
   * 
   * @param clientSocket
   * @throws IOException
   * @throws NoSuchAlgorithmException 
   */
  public static void handshake(Socket clientSocket) throws IOException, NoSuchAlgorithmException {
    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    
    String line = null;
    String key = null;
    
    // Get client handshake.
    while (!(line = input.readLine()).equals("")) {
      StringTokenizer st = new StringTokenizer(line, " ");
      
      if (st.hasMoreTokens() && st.nextToken().equals("Sec-WebSocket-Key:")) {
        key = st.nextToken();
      }
    }
    
    byte[] hash = SHA.encodeToSHA1(key + magicString);
    
    // Send server handshake.
    String[] responseHeader = {
      "HTTP/1.1 101 Switching Protocols",
      "Upgrade: websocket",
      "Connection: Upgrade",
      "Sec-WebSocket-Accept: " + new String(Base64.encodeBase64(hash)),
    };
    
    PrintStream output = new PrintStream(clientSocket.getOutputStream());
    
    for (int i = 0; i < responseHeader.length; i++) {
      output.print(responseHeader[i] + "\r\n");
      //System.out.println(responseHeader[i]);
    }
    output.print("\r\n");
  }
}

package org.websocketchat.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import org.websocketchat.utils.Convert;

/**
 *
 * @author
 * Tamás Nagy
 */
public class WebSocketReader {
  private InputStream input = null;
  
  public WebSocketReader(Socket socket) throws IOException {
    this.input = socket.getInputStream();
  }
  
  /**
   * Reads a message from the socket.
   * 
   * @return
   * @throws IOException 
   */
  public String getMessage() throws IOException {
    int type = this.input.read();
    switch (type) {
      case 129: // blob
        return this.getTextMessage();
      case 130: // arraybuffer
        return this.getArrayMessage();
      default:
        return null;
    }
  }
  
  /**
   * Reads text type message from the socket.
   * 
   * @param input
   * @return String
   * @throws IOException 
   */
  private String getTextMessage() throws IOException {
    int length = this.input.read() - 128;
    
    if (length == 126) {
      byte[] b = {
        (byte)this.input.read(),
        (byte)this.input.read()
      };
      
      length = Convert.byteArrayToInt(b);
    }
    else if (length == 127) {
      byte[] b = {
        (byte)this.input.read(),
        (byte)this.input.read(),
        (byte)this.input.read()
      };
      
      length = Convert.byteArrayToInt(b);
    }
    
    // Get key.
    int key[] = new int[4];
    for (int i = 0; i < 4; i++) {
      key[i] = this.input.read();
    }
    
    String str = new String();
    
    for (int i = 0; i < length; i++) {
      str += (char)(this.input.read() ^ key[i % 4]);
    }
    
    return str;
  }
  
  public void close() throws IOException {
    this.input.close();
  }

  private String getArrayMessage() throws IOException {
    // @TODO:
    
    return "";
  }
}

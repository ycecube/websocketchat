/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.websocketchat.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 *
 * @author
 * yce
 */
class WebSocketWriter {
  Socket socket = null;
  OutputStream output = null;
  
  public WebSocketWriter(Socket socket) throws IOException {
    this.socket = socket;
    this.output = socket.getOutputStream();
  }
  
  public void writeMessage(String msg, int type) throws IOException {
    switch (type) {
      case 129: // text
        this.writeTextMessage(msg);
        break;
    }        
  }

  private void writeTextMessage(String msg) throws IOException {
    Integer length = msg.length();
    
    int extraBytes = 2;
    
    if (length >= 126) {
      extraBytes = 4;
    }
    
    byte[] bytes = new byte[msg.length() + extraBytes];
    
    int index = 0;
    bytes[index++] = (byte)129;
    
    
    // @TODO: 3rd type of length must be added.
    if (length < 126) {
      bytes[index++] = length.byteValue();
    }
    else if (length >= 126){
      bytes[index++] = (byte)126;
      byte[] b = ByteBuffer.allocate(4).putInt(length).array();
      bytes[index++] = b[2];
      bytes[index++] = b[3];
    }
    
    
    char[] chars = msg.toCharArray();

    for (int i = 0; i < chars.length; i++) {
      bytes[index++] = (byte)(chars[i]);
    }
    
    output.write(bytes);
  }
  
  public void close() throws IOException {
    this.output.close();
  }
  
  public boolean isClosed() {
    return this.socket.isClosed();
  }
}

package org.websocketchat.net;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
  * Defines a client class.
  * Every client has its own.
  */
public class Client implements Runnable {
  private Socket socket = null;
  
  WebSocketWriter writer = null;
  
  private TreeMap<Long, String> buffer = new TreeMap<>();

  // User data
  private String nick;

  /**
   *
   * @param socket
   */
  public Client(int id, Socket socket) throws IOException {
    this.nick = "Stranger" + String.valueOf(id);
    this.socket = socket;
    this.writer = new WebSocketWriter(this.socket);
    
    buffer.put(System.currentTimeMillis(), nick + " is joined in.");
  }

  @Override
  public void run() {
    try {
      WebSocketReader r = new WebSocketReader(this.socket);
      
      String msg = null;
      
      do {
        msg = r.getMessage();
        
        // Commands
        if (msg != null && !msg.isEmpty()) {
          StringTokenizer st = new StringTokenizer(msg, " ");
          // Change nick.
          if (st.countTokens() == 2 && st.nextToken().equals("/nick")) {
            String newNick = this.nick + " is now known as ";
            this.nick = st.nextToken();
            newNick += this.nick + ".";
            buffer.put(System.currentTimeMillis(), newNick);
            continue;
          }
        }
        
        buffer.put(System.currentTimeMillis(), nick + ":" + msg);
      } while (msg != null);
      
      socket.close();
      
      r.close();
      r = null;
    } catch (IOException ex) {
      try {
        this.socket.close();
      } catch (IOException ex1) {
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex1);
      }
    }
  }
  
  /**
   * Get the latest message fromm the buffer and delete it.
   * 
   * @return String or null if there is nothing in the buffer.
   */
  public String readBuffer() {
    Map.Entry<Long, String> lastBufferEntry = buffer.lastEntry();
    
    if (!buffer.isEmpty()) {
      buffer.remove(lastBufferEntry.getKey());
    }
    else {
      return null;
    }
    
    return lastBufferEntry.getValue();
  }
  
  public String getNick() {
    return this.nick;
  }
  
  public void writeToClientSocket(String msg) {
    try {
      if (!writer.isClosed()) {
        writer.writeMessage(msg, 129);
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
  }
  
  public boolean isClosed() {
    return this.socket.isClosed();
  }
}
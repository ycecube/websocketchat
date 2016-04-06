package org.websocketchat.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Defines a client manager class.
 * This class takes care of the message handling.
 */
public class ClientManager implements Runnable {
  private ServerSocket socket = null;
  private Socket clientSocket = null;

  private ArrayList<Client> clients = new ArrayList<>();
  
  private int clientID;
  
  public ClientManager(ServerSocket socket) {
    this.socket = socket;
    this.clientID = 1;
  }
  
  @Override
  public void run() {
    Thread msgh = new Thread(new MessageHandler(), "MessageHandler");
    msgh.start();
    
    try {
      while (true) {
        clientSocket = socket.accept();
        WebSocketServer.handshake(clientSocket);
        
        Client client = new Client(this.clientID, clientSocket);
        this.clientID++;
        Thread t = new Thread(client, "Client");
        clients.add(client);
        t.start();
      }
    }
    catch (IOException | NoSuchAlgorithmException ex) {
      System.out.println(ex.getMessage());
    }
  }
  
  private class MessageHandler implements Runnable {
    @Override
    public void run() {
      try {
        while (true) {
          for (int i = 0; i < clients.size(); i++) {
            if (!clients.get(i).isClosed()) {
              String msg = clients.get(i).readBuffer();
            
              if (msg != null) {
                for (int j = 0; j < clients.size(); j++) {
                  clients.get(j).writeToClientSocket(msg);
                }
              }
            }
            else {
              String nick = clients.get(i).getNick();
              for (int j = 0; j < clients.size(); j++) {
                if (i != j) {
                  clients.get(j).writeToClientSocket(nick + " is quited.");
                }
              }
              clients.remove(i);
              break;
            }
          }
          Thread.sleep(100); // @TODO: have to find a better performance solution.
        }
      }
      catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }
  }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socketexamples;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * @author igortn
 */
public class SocketServerThreadPoolUDP {

  public static void main(String[] args) throws Exception {

    byte[] buf = new byte[512];
    boolean running = true;
    // create socket listener
    try (DatagramSocket socket = new DatagramSocket(12031)) {
      socket.setSoTimeout(5000);
      // create object to count active threads
      ActiveCount threadCount = new ActiveCount();
      System.out.println("Server started .....");
      Executor pool = Executors.newFixedThreadPool(8);

      loop:
      while (running) {
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
          socket.receive(packet);
        }
        catch (SocketTimeoutException ex) {
          System.out.println(ex.getMessage());
          break loop;
        }
        pool.execute(new SocketHandlerRunnableUDP(socket, packet, threadCount));
      }
    }
  }

}

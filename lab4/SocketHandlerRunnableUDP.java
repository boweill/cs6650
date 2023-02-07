package socketexamples;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/*
* Simple thread to handle a socket request
* Author: Ian Gorton
*/
class SocketHandlerRunnableUDP implements Runnable {
  private DatagramSocket socket;
  private DatagramPacket packet;
  private boolean running = true;
  private final ActiveCount threadCount;
  private byte[] buf;

  SocketHandlerRunnableUDP(DatagramSocket socket, DatagramPacket packet, ActiveCount threads) {
    this.socket = socket;
    this.packet = packet;
    threadCount = threads;
    buf = new byte[512];
  }

  public void run() {
    threadCount.incrementCount();
    System.out.println("Accepted Client: Address - "
        + packet.getAddress());
    try {

      InetAddress address = packet.getAddress();
      int port = packet.getPort();
      String received = new String(packet.getData(), 0, packet.getLength());
      String reply = "Active Server Thread Count = " + threadCount.getCount();
      buf = reply.getBytes();
      packet = new DatagramPacket(buf, buf.length, address, port);

      socket.send(packet);
    } catch (Exception e) {
           e.printStackTrace();
    }

    threadCount.decrementCount();
    System.out.println("Thread exiting");
  }
  
} //end class

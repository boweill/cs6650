package socketexamples;

/**
 * Simple skeleton socket client thread that coordinates termination
 * with a cyclic barrier to demonstration barrier synchronization
 * @author Ian Gorton
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.crypto.Data;

// Sockets of this class are coordinated  by a CyclicBarrier which pauses all threads 
// until the last one completes. At this stage, all threads terminate

public class SocketClientThreadUDP extends Thread {
    private long clientID;
    String hostName;
    int port;
    CyclicBarrier synk;
    private DatagramSocket s;
    private InetAddress address;
    private byte[] buf;

    private final static int NUM_ITERATIONS = 1000;

    public SocketClientThreadUDP(String hostName, int port, CyclicBarrier barrier, DatagramSocket s) {
        this.hostName = hostName;
        this.port = port;
        clientID = Thread.currentThread().getId();
        synk = barrier;
        this.s = s;
    }

    @Override
    public void run() {

        clientID = Thread.currentThread().getId();
        try{
            address = InetAddress.getByName("localhost");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TO DO insert code to pass  messages to the SocketServer
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            try {
                String msg = "Client ID is " +  Long.toString(clientID);
                buf = msg.getBytes();

                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
                s.send(packet);

                buf = new byte[512];
                packet = new DatagramPacket(buf, buf.length);
                s.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(received);
                System.out.println("read response");

            } catch (UnknownHostException e) {
                // if we get an exception, don't bother retrying
                System.err.println("Don't know about host " + hostName);
                break;
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " +
                    hostName + " " + e.getMessage() + " " + clientID);
                // if we get an exception, don't bother retrying
                break;
            }
        } // end for
        try {
            // TO DO insert code to wait on the CyclicBarrier
            System.out.println("Thread " + clientID + " waiting at barrier");
            synk.await();
        } catch (InterruptedException | BrokenBarrierException ex) {
            Logger.getLogger(SocketClientThreadUDP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

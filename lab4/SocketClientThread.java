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
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

// Sockets of this class are coordinated  by a CyclicBarrier which pauses all threads 
// until the last one completes. At this stage, all threads terminate

public class SocketClientThread extends Thread {
    private long clientID;
    String hostName;
    int port;
    CyclicBarrier synk;
    private Socket s;
    private PrintWriter out;
    private BufferedReader in;

    private final static int NUM_ITERATIONS = 1000;
    
    public SocketClientThread(String hostName, int port, CyclicBarrier barrier) {
        this.hostName = hostName;
        this.port = port;
        clientID = Thread.currentThread().getId();
        synk = barrier;
        
    }

    public void startConnection(String ip, int port) throws IOException {
        s = new Socket(ip, port);
        out =
            new PrintWriter(s.getOutputStream(), true);
        in =
            new BufferedReader(
                new InputStreamReader(s.getInputStream()));

    }

    @Override
    public void run() {
        clientID = Thread.currentThread().getId();
        try{
            startConnection(hostName, port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        // TO DO insert code to pass  messages to the SocketServer
        for (int i = 0; i < NUM_ITERATIONS; i++) {
            try {
                out.println("Client ID is " +  Long.toString(clientID));

                System.out.println(in.readLine());
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
            out.println("bye");
            String resp = in.readLine();
            System.out.println("Server response to connection close request: " + resp);
            // TO DO insert code to wait on the CyclicBarrier
            System.out.println("Thread " + clientID + " waiting at barrier");
            synk.await();
        } catch (InterruptedException | BrokenBarrierException | IOException ex) {
            Logger.getLogger(SocketClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

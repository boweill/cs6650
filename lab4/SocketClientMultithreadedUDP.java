package socketexamples;

/**
 * Skeleton socket client. 
 * Accepts host/port on command line or defaults to localhost/12031
 * Then (should) starts MAX_Threads and waits for them all to terminate before terminating main()
 * @author Ian Gorton
 */

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class SocketClientMultithreadedUDP {
    
    static CyclicBarrier barrier;
    static DatagramSocket socket;
    
    public static void main(String[] args)
        throws BrokenBarrierException, InterruptedException, SocketException {
        String hostName;
        final int MAX_THREADS = 50 ;
        int port;
        
        if (args.length == 2) {
            hostName = args[0];
            port = Integer.parseInt(args[1]);
        } else {
            hostName= null;
            port = 12031;  // default port in SocketServer
        }
        barrier = new CyclicBarrier(MAX_THREADS + 1, new Runnable() {
            @Override
            public void run() {
                System.out.println("We finished sending 1k requests!");
            }
        });

        socket = new DatagramSocket();
        long start = System.currentTimeMillis();
        // TO DO create and start MAX_THREADS SocketClientThread
        for (int i=0; i < MAX_THREADS; i++){
            new SocketClientThreadUDP(hostName, port, barrier, socket).start();
        }

        // TO DO wait for all threads to complete
        barrier.await();

        System.out.println("Time elapsed: " + (System.currentTimeMillis() - start));

        System.out.println("Terminating ....");
                
    }

      
}
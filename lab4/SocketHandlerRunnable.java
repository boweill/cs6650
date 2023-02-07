package socketexamples;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/*
* Simple thread to handle a socket request
* Author: Ian Gorton
*/
class SocketHandlerRunnable implements Runnable {
  private final Socket clientSocket;
  private boolean running = true;
  private final ActiveCount threadCount;
  private PrintWriter out;
  private BufferedReader in;

  SocketHandlerRunnable(Socket s, ActiveCount threads) {
    clientSocket = s;
    threadCount = threads;
  }

  public void run() {
    threadCount.incrementCount();
    System.out.println("Accepted Client: Address - "
        + clientSocket.getInetAddress().getHostName());
    try {

      out = new PrintWriter(clientSocket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      String inputLine;
      while((inputLine = in.readLine()) != null) {
       System.out.println("Handler: " + inputLine);
        if ("bye".equals(inputLine)) {
          out.println("bye");
          break;
        }
        System.out.println("Client ID is :" + inputLine);
        out.println("Active Server Thread Count = " + threadCount.getCount());
        out.flush();
        System.out.println("Reply sent");
      }
    } catch (Exception e) {
           e.printStackTrace();
    }

    threadCount.decrementCount();
    System.out.println("Thread exiting");
  }
  
} //end class

package Core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;	

public class ServerThreadPool {
	private static int THREAD_NUM = 4; 		// Maximum worker thread in the pool
	private static boolean listen = true;	// Control flag for listen thread
	private static int DEFAULT_WEL_PORT = 8080; //Assign 8080 to be default welcome port
	
	
	//Ask the listen thread to stop listening 
	public static void shutdown() {
		listen = false;
	}
	
	public static void main(String[] args) throws Exception {
		P2PLogger logger = new P2PLogger();
		logger.setPrefix("ServerThreadPool");
		ServerSocket welcomeSocket = new ServerSocket(DEFAULT_WEL_PORT);
		ExecutorService ServerThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
		
		logger.append("ServerThreadPool established.");
		
		try {
			while(listen) {
				try {
					// Main thread listen for connection request to welcome socket
					logger.append("Main socket ready to listen on welcome socket.");
					logger.log();
					Socket connectionSocket = welcomeSocket.accept(); 
					// When actual connection is established, let connection socket handle
					Runnable workerThread = new ServerWorkerThread(connectionSocket);
					ServerThreadPool.execute(workerThread);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//Ensure every thread and welcome socket is closed.
		finally {
			ServerThreadPool.shutdownNow();
			welcomeSocket.close();
		}
	}
}

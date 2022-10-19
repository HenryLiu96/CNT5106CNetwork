package Core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServerThreadPool {
	private static int THREAD_NUM = 4; 		// Maximum worker thread in the pool
	private static boolean listen = true;	// Control flag for listen thread
	private static int DEFAULT_WEL_PORT = 8080; //Assign 8080 to be default welcome port
	
	//Ask the listen thread to stop listening 
	public static void shutdown() {
		listen = false;
	}
	
	public static void main(String[] args) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(DEFAULT_WEL_PORT);
		ExecutorService ServerThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
		
		try {
		while(listen) {
			try {
				Socket connectionSocket = welcomeSocket.accept();
				Thread workerThread = new Thread(
						new ServerWorkerThread(connectionSocket));
				ServerThreadPool.execute(workerThread);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		}finally {
			ServerThreadPool.shutdownNow();//
			welcomeSocket.close();
		}
	}
}

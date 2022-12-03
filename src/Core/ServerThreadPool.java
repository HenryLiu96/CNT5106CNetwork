package Core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Util.Config;	

public class ServerThreadPool {
	private static int THREAD_NUM = 4; 		// Maximum worker thread in the pool
	private static boolean listen = true;	// Control flag for listen thread
	private static int DEFAULT_WEL_PORT = 8080; //Assign 8080 to be default welcome port
	private static int k = 4;				// Number of Preferred neighbor
	private static int p = 6;				// Number of sec before repicking PN
	private static int m = 12;				// Number of 
	
	/**Initialization of Thread pool
		1. Read k(Number of Preferred neighbors) from configuration file 
		2. Read p(Unchoking interval) from configuration file file
		3. Read m(Optimistic unchoking interval) from config file
	 * @throws Exception 
	*/
	public static void init() throws Exception {
		String workingDir = Paths.get("").toAbsolutePath().toString();
		String cfgPath = workingDir.concat(Routes.CONFIG_DIR).concat(Routes.COMMON_CFG);
		Config config  =  new Config(cfgPath);
		k = config.getInt("NumberOfPreferedNeighbours");
		p = config.getInt("UnchokingInterval");
		m = config.getInt("OptimisticUnchokingInterval");
	}
	
	/**
	 * Pick preferred 
	 */
	public static void pickPrefer() {
		//TODO
	}
	/**
	 * Choose optimistically unchoke
	 */
	public static void pickOpt() {
		//TODO
	}
	
	//Ask the listen thread to stop listening 
	public static void shutdown() {
		listen = false;
	}
	
	public static void main(String[] args) throws Exception {
		init();
		
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

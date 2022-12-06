package Core;

import java.io.File;
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
	public static int k = 4;				// Number of Preferred neighbor
	public static int p = 6;				// Number of sec before repicking PN
	public static int m = 12;				// Number of sec before repicking optimistic unchoking neighbor
	public static String file_name = "f";	// Name of the file
	public static int file_size = 100;		// File size
	public static int piece_size = 10;		// Piece size


	/**Initialization of Thread pool
		1. Read k(Number of Preferred neighbors) from configuration file 
		2. Read p(Unchoking interval) from configuration file
		3. Read m(Optimistic unchoking interval) from config file
	 	4. Read file_name from config file
	 	5. Read file_size from config file
	 	6. Read piece_size from config file
	 * @throws Exception 
	*/
	public static void init() throws Exception {
		String cfgPath = "./Config/common.cfg";
		Config config  =  new Config(cfgPath);
		k = config.getInt("NumberOfPreferredNeighbors");
		p = config.getInt("UnchokingInterval");
		m = config.getInt("OptimisticUnchokingInterval");
		file_name = config.getString("FileName");
		file_size = config.getInt("FileSize");
		piece_size = config.getInt("PieceSize");
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
		// read configuration file: common.cfg and save parameters (tested)
		init();
		// read peer information file: PeerInfo.cfg and save peer info to list (tested)
		PeerInfoHandler pif = new PeerInfoHandler();
		FileHandler fh = new FileHandler();
		for (PeerInfo peerInfo : pif.getPeerInfoList()) {
			System.out.println("connect to " + peerInfo.getHost() + " " + peerInfo.getPort());
			fh.setFile(peerInfo);
		}


//		P2PLogger logger = new P2PLogger();
//		logger.setPrefix("ServerThreadPool");
//		ServerSocket welcomeSocket = new ServerSocket(DEFAULT_WEL_PORT);
//		ExecutorService ServerThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
//
//		logger.append("ServerThreadPool established.");
//
//		try {
//			while(listen) {
//				try {
//					// Main thread listen for connection request to welcome socket
//					logger.append("Main socket ready to listen on welcome socket.");
//					logger.log();
//					Socket connectionSocket = welcomeSocket.accept();
//					// When actual connection is established, let connection socket handle
//					Runnable workerThread = new ServerWorkerThread(connectionSocket);
//					ServerThreadPool.execute(workerThread);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		//Ensure every thread and welcome socket is closed.
//		finally {
//			ServerThreadPool.shutdownNow();
//			welcomeSocket.close();
//		}
	}
}

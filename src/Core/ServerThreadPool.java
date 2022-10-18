package Core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerThreadPool {
	private static int THREAD_NUM = 4;
	private static boolean listen = true;
	
	
	
	public static void shutdown() {
		listen = false;
	}
	
	public static void main(String[] args) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(8080);
		ExecutorService ServerThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
		//Listening thread
		ServerThreadPool.submit(new Runnable() {
		@Override
		public void run() {
			while(listen) {
				try {
					Socket connectionSocket = welcomeSocket.accept();
					Thread workerThread = new Thread(
							new ServerWorkerThread(connectionSocket));
					workerThread.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}});
		welcomeSocket.close();
	}
}

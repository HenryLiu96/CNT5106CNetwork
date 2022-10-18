package Core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerThreadPool {
	private static int THREAD_NUM = 4;
	
	public static void main(String[] args) throws Exception {
		
		ServerSocket welcomeSocket = new ServerSocket(8080);
		ExecutorService ServerThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
		
		for(int i=0; i<1; i++) {
			ServerThreadPool.submit(new Runnable() 
			{
					@Override
					public void run() {
						while(true) {
							try {
								Socket connectionSocket = welcomeSocket.accept();
								
								InputStreamReader readFrom8080 = new 
										InputStreamReader(connectionSocket.getInputStream());
								BufferedReader welcomeInStream = new 
										BufferedReader(readFrom8080);
								String handshakeReq = welcomeInStream.readLine();
								//TODO: Implement check handshake request
								
								
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						//TODO listen to welcome socket
					}
					});
		}
		
		for(int i=0; i<3; i++) {
			ServerThreadPool.submit(new Runnable() 
			{
					@Override
					public void run() {
						//TODO Do something else
					}
					});
		}
	}
}

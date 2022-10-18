package Core;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerThreadPool {
	private static int THREAD_NUM = 4;
	private static boolean workerThreadRun = false;
	
	public void shutdown() {
		this.workerThreadRun = false;
	}
	
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
						HandShakeMessage hsm = new HandShakeMessage(handshakeReq);
						if(!hsm.permitToConnect()) {
							throw new IOException("Forbid to connect by server peer.");
						}
					
						OutputStream output = connectionSocket.getOutputStream();
						DataOutputStream writeToClientStream = new DataOutputStream(output);
						writeToClientStream.writeBytes("");
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
						while(workerThreadRun) {
							
						}
						//TODO Do something else
					}
					});
		}
	}
}

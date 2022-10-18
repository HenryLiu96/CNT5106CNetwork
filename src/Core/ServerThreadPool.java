package Core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerThreadPool {
	private static int THREAD_NUM = 4;
	
	public static void main(String[] args) {
		ExecutorService ServerThreadPool = Executors.newFixedThreadPool(THREAD_NUM);
		
		for(int i=0; i<1; i++) {
			ServerThreadPool.submit(new Runnable() {
					@Override
					public void run() {
						//TODO listen to welcome socket
					}
					});
		}
		
		for(int i=0; i<3; i++) {
			ServerThreadPool.submit(new Runnable() {
					@Override
					public void run() {
						//TODO Do something else
					}
					});
		}
	}

}

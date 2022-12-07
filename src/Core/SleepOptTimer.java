package Core;

import java.util.Date;

public class SleepOptTimer implements Runnable{
	private boolean kill = false;
	private int interval;
	
	public SleepOptTimer(int interval) {
		this.interval = interval*1000;
	}
	
	@Override
	public void run() {
		while(!kill) {
			try {
				//TODO invoke  Update
				//TODO log Preferred Neighbor Update
				Date current = new Date(System.currentTimeMillis());
				System.out.println(current.toGMTString());
				Thread.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

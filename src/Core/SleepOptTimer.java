package Core;

import java.util.Date;

public class SleepOptTimer implements Runnable{
	private static final String LOG_Format = " Peer [%d] has the optimistically "
			+ "unchoked neighbors [%d].";
	
	private boolean kill = false;
	private int interval;
	private int peerID;
	private int optUnchokedNeighbor;
	
	P2PLogger logger;
	
	public SleepOptTimer(int interval) {
		this.interval = interval*1000;
		this.peerID = Integer.valueOf(ServerThreadPool.getPeerID());
		this.optUnchokedNeighbor = ServerThreadPool.getStatusMap().getOptUnchokedNeighbor();
	}
	
	@Override
	public void run() {
		try {
			logger = new P2PLogger();
			
			while(!kill) {
				//TODO invoke  Update
				//TODO log Preferred Neighbor Update
//				Date current = new Date(System.currentTimeMillis());
//				System.out.println(current.toGMTString());
				
				logger.append(String.format(LOG_Format, this.peerID, this.optUnchokedNeighbor));
				logger.log();
				Thread.sleep(interval);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}

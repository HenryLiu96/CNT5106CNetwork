package Core;

import java.util.Date;

public class SleepPNTimer implements Runnable {
	private static final String LOG_Format = " Peer [%d] has the preferred "
											+ "neighbors [%s]";
	
	private boolean kill = false;
	private int interval;
	

	P2PLogger logger;

	public SleepPNTimer(int interval) {
		this.interval = interval * 1000;
	}

	@Override
	public void run() {
		try {
			logger = new P2PLogger();
			while (!kill) {
				// TODO get this peer ID
				int PeerID = 10086;
				// TODO invoke Preferred Neighbor Update
				// TODO log Preferred Neighbor Update
				String PeerListStr = "DEMO Peer list string";
				
				logger.append(String.format(LOG_Format, PeerID, PeerListStr));
				logger.log();
				Thread.sleep(interval);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}



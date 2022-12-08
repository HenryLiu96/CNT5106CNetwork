package Core;

import java.util.HashMap;

public class SleepPNTimer implements Runnable {
	private static final String LOG_Format = " Peer [%d] has the preferred "
											+ "neighbors [%s].";
	
	private boolean kill = false;
	private int interval;
	private PeerStatusMap statusMap;
	private int peerID;

	P2PLogger logger;

	public SleepPNTimer(int interval) {
		this.interval = interval * 1000;
		this.peerID = Integer.valueOf(ServerThreadPool.getPeerID());
		this.statusMap = ServerThreadPool.getStatusMap();
	}

	@Override
	public void run() {
		try {
			logger = new P2PLogger();
			
			while (!kill) {
				// TODO get this peer ID
				// TODO invoke Preferred Neighbor Update
				// Log Preferred Neighbor Update
				String PeerListStr = composeListStr();
				
				logger.append(String.format(LOG_Format, this.peerID, PeerListStr));
				logger.log();
				Thread.sleep(interval);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * TODO
	 * @return
	 */
	private String composeListStr() {
		StringBuilder listSb = new StringBuilder();
		
		HashMap<String, Boolean> ChokeStatus = this.statusMap.getChokeStatus();
		if(!ChokeStatus.isEmpty()) {
			for(String peerID : ChokeStatus.keySet()){
			    if(!ChokeStatus.get(peerID)){
			        listSb.append(peerID);
			        listSb.append(",");
			    }
			}
			listSb.delete(listSb.length() - 1, listSb.length());
			
		}
		
		String PeerListStr = listSb.toString();
		return PeerListStr;
	}
}



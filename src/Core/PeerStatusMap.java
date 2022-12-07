package Core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import Util.Config;

public class PeerStatusMap {
	private static String configPath = "./Config/PeerInfo.cfg";
	private static String comConfigPath = "./Config/common.cfg";
	private static int pieceNum = -1;
	
	private HashMap<String, boolean[]> peerBitMap = new HashMap<>();
	private HashMap<String, Boolean> chokeStatus = new HashMap<>();
	
	private HashSet<Integer> peerSet = new HashSet<>();
	private HashSet<Integer> interestSet = new HashSet<>();
	
	private int[][] pieceRequested;
	private int optUnchokedNeighbor = -1;
	
	private int k = 3;

	public PeerStatusMap() throws Exception {
		Config peerConf = new Config(configPath);
		Config comConf = new Config(comConfigPath);
		
		//TODO common config set K
		pieceNum = (int)Math.ceil((float)comConf.getInt("FileSize")
				/comConf.getInt("PieceSize"));
		pieceRequested = new int[pieceNum][2];
		
		for(Entry<Object, Object> e: peerConf.entrySet()) {
			String peerID = e.getKey().toString();
			boolean hasFile = e.getValue().toString().trim().endsWith("1");
			System.out.println(peerID);
			System.out.println(hasFile);
			boolean[] bitField = new boolean[pieceNum];
			if(hasFile) {//TODO: determine if have file
				Arrays.fill(bitField, true);
			}
			peerBitMap.put(peerID, bitField);
			peerSet.add(Integer.valueOf(peerID));
		}
		
	}
	
	synchronized public HashMap<String, Boolean> getChokeStatus(){
		return this.chokeStatus;
	}
	
	synchronized public boolean[] getBitField(String peerID) throws Exception {
		if(!this.peerBitMap.containsKey(peerID)) {
			throw new Exception("THIS PEER ID SUCKS!");//TODO
		}else {
			return this.peerBitMap.get(peerID);			
		}
	}
	
	synchronized public boolean[] getBitField(int peerID) throws Exception {
		if(!this.peerBitMap.containsKey(String.valueOf(peerID))) {
			throw new Exception("THIS PEER ID SUCKS!");//TODO
		}else {
			return this.peerBitMap.get(String.valueOf(peerID));			
		}
	}
	
	synchronized public void updateBitField(int peerID, int index) throws Exception {
		if(index >=  this.pieceNum || index < 0) {
			throw new Exception("Illegal Index!"); //TODO
		}
		if(!this.peerBitMap.containsKey(String.valueOf(peerID))) {
			throw new Exception("Don't have this peer!"); //TODO
		}
		this.peerBitMap.get(String.valueOf(peerID))[index] = true;
	}
	
	synchronized public HashSet<Integer> getPeerSet(){
		return this.peerSet;
	}
	
	synchronized public void addInterestedPeer(int peerID) {
		interestSet.add(peerID);
	}
	
	synchronized public void removeInterest(int peerID) {
		interestSet.remove(peerID);
	}
	
	synchronized public HashSet<Integer> getInterestedSet(){
		return this.interestSet;
	}
	
	synchronized public void setChoked(int peerID) {
		this.chokeStatus.put(String.valueOf(peerID), true);
	}
	
	synchronized public void setUnchoked(int peerID) {
		this.chokeStatus.put(String.valueOf(peerID), false);
	}
	
	synchronized public void addRequest(int index, int peerID) {
		this.pieceRequested[index][0] = 1;
		this.pieceRequested[index][1] = peerID;
	}
	
	synchronized public void requestDownloaded(int index) {
		this.pieceRequested[index][0] = 2;
	}
	
	synchronized public void clearChokeRequest(int peerID) {
		for(int i=0; i<pieceNum; i++) {
			if(this.pieceRequested[i][0]==1 && this.pieceRequested[i][1]==peerID) {
				this.pieceRequested[i][1]=-1;
				this.pieceRequested[i][0]= 0;
			}
		}
	}
	
	synchronized public void pickOptUnchokeNeighbor(){
		List<Integer> chokedAndInterested = new ArrayList<>();
		for(int peerID: interestSet) {
			if(chokeStatus.get(String.valueOf(peerID))) {
				chokedAndInterested.add(peerID);
			}
		}
		if(chokedAndInterested.size() != 0) {
			Collections.shuffle(chokedAndInterested);
			this.optUnchokedNeighbor = chokedAndInterested.get(0);
		}
	}
	
	synchronized public int getOptUnchokedNeighbor() {
		return this.optUnchokedNeighbor;
	}
	
	synchronized public PriorityQueue<int[]> genPriorityQueue() {
		Map<Integer, Integer> downloadTimes = new HashMap<>();
		for (int i = 0; i < pieceNum; i++) {
			if(pieceRequested[i][0] == 2){
                downloadTimes.put(pieceRequested[i][1],  
                		downloadTimes.getOrDefault(pieceRequested[i][1], 0) + 1);
            }
		}
		
		// add 0 download-time Peer to map
        for(int peerID : ServerThreadPool.getStatusMap().getPeerSet()){
            if(downloadTimes.containsKey(peerID)){
                continue;
            }else{
                downloadTimes.put(peerID, downloadTimes.getOrDefault(peerID, 0));
            }
        }
        
        List<Integer> IDs = new ArrayList<>(downloadTimes.keySet());
        Collections.shuffle(IDs);
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> (b[1] - a[1]));
        for(int peerID : IDs){
            pq.add(new int[]{peerID, downloadTimes.get(peerID)});
        }
        
        return pq;
	}
	
	synchronized public void pickPreferredNeighbor(PriorityQueue<int[]> pq) {
		int count = 0;
		while(!pq.isEmpty()) {
			int[] pair = pq.poll(); // pair of peerID and download pieces
			if(count < this.k) { 	// PN space not filled
				if(interestSet.contains(pair[0])) {// Peer interested in this peer
					count++;
					if(chokeStatus.get(String.valueOf(pair[0]))) {
						//TODO sendUnchoke: Invoke Message Handler
						this.chokeStatus.put(String.valueOf(pair[0]), false);
					}
				}
			} 
			else {				// PN space filled
				if(pair[0] != optUnchokedNeighbor) {
					if(!chokeStatus.get(String.valueOf(pair[0]))) {
						//TODO sendChoke: Invoke Message Handler
						this.chokeStatus.put(String.valueOf(pair[0]), true);
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
//		List<String> pref = new ArrayList<>();
//		pref.add("1001");
//		pref.add("1002")
		PeerStatusMap psm = new PeerStatusMap();
		boolean[] bArr = psm.peerBitMap.get("1003");
		for(boolean b: bArr) {
			System.out.println(b);
		}
		for(int a: psm.peerSet) {
			System.out.println(a);
		}
		
	}
	
}

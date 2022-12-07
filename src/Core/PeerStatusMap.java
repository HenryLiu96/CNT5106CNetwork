package Core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import Util.Config;

public class PeerStatusMap {
	private static String configPath = "./Config/PeerInfo.cfg";
	private static String comConfigPath = "./Config/common.cfg";
	private static int pieceNum = -1;
	
	private HashMap<String, boolean[]> peerBitMap = new HashMap<>();
	private HashMap<String, Boolean> chokeStatus = new HashMap<>();
	
	public PeerStatusMap() throws Exception {
		Config peerConf = new Config(configPath);
		Config comConf = new Config(comConfigPath);
		
		pieceNum = (int)Math.ceil((float)comConf.getInt("FileSize")
				/comConf.getInt("PieceSize"));
		
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
			
		}
		
	}
	
	public boolean[] getBitField(String peerID) throws Exception {
		if(!this.peerBitMap.containsKey(peerID)) {
			throw new Exception("THIS PEER ID SUCKS!");//TODO
		}else {
			return this.peerBitMap.get(peerID);			
		}
	}
	
	public boolean[] getBitField(int peerID) throws Exception {
		if(!this.peerBitMap.containsKey(String.valueOf(peerID))) {
			throw new Exception("THIS PEER ID SUCKS!");//TODO
		}else {
			return this.peerBitMap.get(String.valueOf(peerID));			
		}
	}
	
	public void updateBitField(int peerID, int index) throws Exception {
		if(index >=  this.pieceNum || index < 0) {
			throw new Exception("Illegal Index!"); //TODO
		}
		if(!this.peerBitMap.containsKey(String.valueOf(peerID))) {
			throw new Exception("Don't have this peer!"); //TODO
		}
		this.peerBitMap.get(String.valueOf(peerID))[index] = true;
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
	}
	
}

package Core;

import java.util.Hashtable;

public class P2PMessage {
	final msgType CHOKE = msgType.choke;
	final msgType UNCHOKE = msgType.unchoke;
	final msgType INTERESTED = msgType.interested;
	final msgType NOT_INTERESTED = msgType.notInterested;
	final msgType HAVE = msgType.have;
	final msgType BITFIELD = msgType.bitField;
	final msgType REQUEST = msgType.request;
	final msgType PRICE = msgType.price;
	
	// Optional message type for actual message
	enum msgType{
		choke,
		unchoke,
		interested,
		notInterested,
		have,
		bitField,
		request,
		price
	}
	
	private int msgLength;
	private msgType type;
	private String payload;
	
	// Constructor
	public P2PMessage(int length, msgType type, String payload) {
		this.msgLength = length;
		this.type=type;
		this.payload=payload;
	}
	
	// Alternative constructor 
	public P2PMessage(int length, msgType type) {
		if(type==CHOKE ||
		   type==UNCHOKE ||
		   type==INTERESTED ||
		   type==NOT_INTERESTED) {
			this.type = type;
			this.msgLength = length;
		}
	}
	
	// Encode enumeration type to integer
	public static int typeEncode(msgType type) {
		Hashtable<msgType, Integer> typeDictionary = new Hashtable<msgType, Integer>();
		typeDictionary.put(msgType.choke, 			1);
		typeDictionary.put(msgType.unchoke, 		2);
		typeDictionary.put(msgType.interested, 		3);
		typeDictionary.put(msgType.notInterested, 	4);
		typeDictionary.put(msgType.have, 			5);
		typeDictionary.put(msgType.bitField, 		6);
		typeDictionary.put(msgType.request, 		7);
		typeDictionary.put(msgType.price, 			8);
		return typeDictionary.get(type);
	}
	
	// To ensure the request sent will be in legal form
	public boolean requestCheck(String s) {
		//TODO check that request message is 4-bytes long piece index field
		
		return true;
	}
	
	// Build the piece payload according to the index requested
	public String buildPiece(String index) {
		StringBuffer sb = new StringBuffer();
		
		//TODO retrieve content of corresponding index
		String pieceContent = "";
		
		return index+"";
	}
	
	/**
	 * Construct the a string message with the three fields
	 */
	public String toString() {
		// TODO Do initialization check
		// TODO Do format check
		
		StringBuffer sb = new StringBuffer();
		
		sb.append(String.valueOf(this.msgLength));
		sb.append(String.valueOf(typeEncode(this.type)));
		sb.append(String.valueOf(this.payload));
		
		return sb.toString();
	}
	
	/**
	 * Transform the String format to Bytes to be send by output stream
	 * @return
	 */
	public byte[] toBytes() {
		byte[] bytes = this.toString().getBytes();
		
		return bytes; 
	}
	
	public static void main(String[] args) {//Empty
	}
}

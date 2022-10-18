package Core;

import java.util.Hashtable;

public class P2PMessage {
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
	
	//Constructor
	public P2PMessage(int length, msgType type, String payload) {
		this.msgLength = length;
		this.type=type;
		this.payload=payload;
	}
	
	//Encode enumeration type to integer
	public static int typeToValue(msgType type) {
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
}

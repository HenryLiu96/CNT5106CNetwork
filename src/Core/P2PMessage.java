package Core;

import java.nio.ByteBuffer;
import java.util.*;

public class P2PMessage {
	final msgType CHOKE = msgType.choke;
	final msgType UNCHOKE = msgType.unchoke;
	final msgType INTERESTED = msgType.interested;
	final msgType NOT_INTERESTED = msgType.notInterested;
	final msgType HAVE = msgType.have;
	final msgType BITFIELD = msgType.bitField;
	final msgType REQUEST = msgType.request;
	final msgType PIECE = msgType.piece;



	// Optional message type for actual message
	enum msgType{
		choke,
		unchoke,
		interested,
		notInterested,
		have,
		bitField,
		request,
		piece
	}
	
	private int msgLength;
	private msgType type;
	private int index;
	private String bitField;
	private byte[] payload;
	private int numOfPiece;



	// Constructor
	public P2PMessage() {

	}

	// Alternative constructor with parameters
	// Chock, unchocked, interested, not interested
	public P2PMessage(msgType type){
		this.type = type;
	}

	//constructor with bit field
	public P2PMessage(msgType type, String bf){
		this.type = type;
		this.bitField = bf;
	}

	public P2PMessage(msgType type, int index){
		this.type = type;
		this.index = index;
	}

	// Constructor for have, request, and piece
	public P2PMessage(msgType type, int payloadIndex, byte[] payload){
		this.type = type;
		this.index = payloadIndex;
		this.payload = payload;
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
		typeDictionary.put(msgType.piece, 			8);
		return typeDictionary.get(type);
	}
	
	// To ensure the index sent will be in legal form
	// request check at first
	public boolean indexCheck(String s) {
		//TODO check that request message is 4-bytes long piece index field
		if(s.length() != 32)
			return false;
		return Integer.parseInt(s, 2) >= 0 && Integer.parseInt(s, 2) <= numOfPiece;
	}


	/**
	 * Construct a string message with the three fields
	 */
	public String toString() {
		// TODO Do initialization check


		// TODO Do format check


		StringBuffer sb = new StringBuffer();

		sb.append(String.valueOf(this.msgLength));
		sb.append(String.valueOf(typeEncode(this.type)));
		return sb.toString();
	}



	// get index of the message
	public int getIndex(){
		return this.index;
	}

	// get payload of the message
	public byte[] getPayload(){
		return this.payload;
	}

	// get bitField
	public String getBitField(){ return this.bitField;}

	// get total number of pieces
	public int getNumOfPiece(){
		this.numOfPiece = ServerThreadPool.file_size / ServerThreadPool.piece_size;
		if(ServerThreadPool.file_size % ServerThreadPool.piece_size != 0){
			numOfPiece++;
		}
		return numOfPiece;
	}

	// get file size
	public int getFileSize(){
		return ServerThreadPool.file_size;
	}

	// get piece size
	public int getPieceSize(){
		return ServerThreadPool.piece_size;
	}


	/**
	 * Transform the bitfield string format to byte
	 * transfer each 8 character in bitfield string to 1 byte
	 * spare bits at the end are set to zeros
	 * @return
	 */
	public static byte[] convertBitFieldToByte(String str) {
		int length = str.length() / 8;
		if(str.length() % 8 != 0){
			length++;
		}
		byte[] bytes = new byte[length];
		for(int i = 0; i < str.length() / 8; i++){
			int cur = Integer.parseInt(str.substring(i * 8, (i + 1) * 8), 2);
			bytes[i] = (byte) cur;
		}
		//build the last string
		if(str.length() % 8 != 0){
			int numOfZeros = 8 - str.length() % 8;
			String lastString = str.substring(8 * (str.length() / 8));
			StringBuilder sb = new StringBuilder(lastString);
			for(int i = 0; i < numOfZeros; i++){
				sb.append('0');
			}
			String last = sb.toString();
			int lastInt = Integer.parseInt(last, 2);
			bytes[length - 1] = (byte)lastInt;
		}
		return bytes;
	}


	/**
	 * Transform the integer format to Bytes to be sent by output stream
	 * length is how many byte we want to convert to
	 * @return
	 */
	public static byte[] convertIntToByte(int num, int length){
		return ByteBuffer.allocate(length).putInt(num).array();
	}

	/**
	 * Transform the byte format to int
	 * @return
	 */
	public static int convertByteToInt(byte[] bytes){
		return ByteBuffer.wrap(bytes).getInt();
	}

	/**
	 * Transform the byte format to string
	 * @return
	 */
	public static String convertByteToBitField(byte[] curBitFieldByte) {
		int numOfPiece = ServerThreadPool.file_size / ServerThreadPool.piece_size;
		if(ServerThreadPool.file_size % ServerThreadPool.piece_size != 0){
			numOfPiece++;
		}
		int numOfZeros = curBitFieldByte.length * 8 - numOfPiece;
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < curBitFieldByte.length; i++){
			int cur = curBitFieldByte[i] & 0xff;
			sb.append(Integer.toBinaryString(cur));
		}
		for(int i = 0; i < numOfZeros; i++){
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}



	/**
	 * Combine two byte array as one
	 * @return
	 */
	public byte[] combineBytes(byte[] b1, byte[] b2){
		byte[] combined = new byte[b1.length + b2.length];
		for(int i = 0; i < combined.length; i++){
			combined[i] = i < b1.length ? b1[i] : b2[i - b1.length];
		}
		return combined;
	}


	/**
	 *
	 *
	 */
	public byte[] messageToBytes(){
		byte[] lengthByte = new byte[4];
		byte[] typeByte = convertIntToByte(typeEncode(this.type), 1);

		// 1 - 4 don't have payload, only length of the message and type, length = 1
		// 5 - 8 have payload
		// 6 - bitfield has a 2D array as payload, length = ?
		// 5 and 7 have piece index as payload, length = 5
		// 8 - piece have piece index and piece content as payload, length = 5 + length of content
		if(typeEncode(this.type) <= typeEncode(msgType.notInterested)){
			// 1 - 4
			lengthByte = convertIntToByte(1, 4);
			return combineBytes(lengthByte, typeByte);
		} else if(typeEncode(this.type) == typeEncode(msgType.have) || typeEncode(this.type) == typeEncode(msgType.request)){
			// 5 and 7
			lengthByte = convertIntToByte(5, 4);
			byte[] lengthAndType = combineBytes(lengthByte, typeByte);
			byte[] indexByte = convertIntToByte(this.index, 4);
			return combineBytes(lengthAndType, indexByte);
		} else if(typeEncode(this.type) == typeEncode(msgType.piece)){
			//8
			byte[] payloadByte = this.payload;
			lengthByte = convertIntToByte(5 + payloadByte.length, 4);
			byte[] lengthAndType = combineBytes(lengthByte, typeByte);
			byte[] indexByte = convertIntToByte(this.index, 4);
			byte[] indexAndPayload = combineBytes(indexByte, payloadByte);
			return combineBytes(lengthAndType, indexAndPayload);
		} else{
			//6 bitfield as string, combine with length and type
			byte[] bitFieldByte = convertBitFieldToByte(bitField);
			lengthByte = convertIntToByte(5 + bitFieldByte.length, 4);
			return combineBytes(lengthByte, typeByte);
		}

	}


	//transfer bytes to P2PMessage and call handlers
	public static P2PMessage byteToMessage(byte[] realMessage, int curPeerID, int senderID){
		//check type of the message
		// 0 - 3 is the length of the message
		// 4 is the type of the message
		// 5 - 8 is the index of the piece for HAVE, REQUEST and PIECE
		// 9 - end is the piece content of the message(payload)
		// 5 - end is the bitfield for BITFIELD
		byte[] curTypeByte = Arrays.copyOfRange(realMessage, 4, 5);
		int curTypeInt = convertByteToInt(curTypeByte);
		byte[] lengthByte = Arrays.copyOfRange(realMessage, 0, 4);
		int lengthOfMessage = convertByteToInt(lengthByte);

		P2PMessage receivedMessage;
		if(curTypeInt == typeEncode(msgType.choke)){
			//receive chock
			receivedMessage = new P2PMessage(msgType.choke);
		}else if (curTypeInt == typeEncode(msgType.unchoke)){
			//receive unchocked
			receivedMessage = new P2PMessage(msgType.unchoke);
		}else if (curTypeInt == typeEncode(msgType.interested)){
			// receive interested
			receivedMessage = new P2PMessage(msgType.interested);
		}else if (curTypeInt == typeEncode(msgType.notInterested)){
			// receive not interested
			receivedMessage = new P2PMessage(msgType.notInterested);
		}else if (curTypeInt == typeEncode(msgType.have)){
			// receive have message
			byte[] curIndexByte = Arrays.copyOfRange(realMessage, 5, 9);
			int curIndex = convertByteToInt(curIndexByte);
			receivedMessage = new P2PMessage(msgType.have, curIndex);
		}else if (curTypeInt == typeEncode(msgType.bitField)){
			// receive bitfield
			byte[] curBitFieldByte = Arrays.copyOfRange(realMessage, 5, lengthOfMessage);
			String curBitField = convertByteToBitField(curBitFieldByte);
			receivedMessage = new P2PMessage(msgType.bitField, curBitField);
		}else if (curTypeInt == typeEncode(msgType.request)){
			//receive request message
			byte[] curIndexByte = Arrays.copyOfRange(realMessage, 5, 9);
			int curIndex = convertByteToInt(curIndexByte);
			receivedMessage = new P2PMessage(msgType.request, curIndex);
		}else{
			//receive piece
			byte[] curIndexByte = Arrays.copyOfRange(realMessage, 5, 9);
			int curIndex = convertByteToInt(curIndexByte);
			byte[] curPayload = Arrays.copyOfRange(realMessage, 9, lengthOfMessage);
			receivedMessage = new P2PMessage(msgType.piece, curIndex, curPayload);
		}
		//TODO figure out the parameter
//		P2PMessageHandler.receiveInterested(receivedMessage, curPeerID, senderID);
		return receivedMessage;
	}



	public static void main(String[] args) {//Empty

	}
}

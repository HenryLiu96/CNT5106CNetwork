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

	//TODO read file size and piece size from config
	final int fileSize = 2167705;
	final int pieceSize = 16384;
	final int numOfPiece = fileSize / pieceSize + 1;

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
	
	// Build the piece payload according to the index requested
	public String buildPiece(String index) {
		StringBuffer sb = new StringBuffer();

		//transfer index from binary to decimal
		int i = Integer.parseInt(index, 2);
		//TODO retrieve content of corresponding index

		String pieceContent = "";
		this.msgLength += pieceContent.length();
		return index + pieceContent;
	}

	public String buildBitField(Map<Integer, String> bitField){
		return "";
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
		return numOfPiece;
	}

	// get file size
	public int getFileSize(){
		return fileSize;
	}

	// get piece size
	public int getPieceSize(){
		return pieceSize;
	}


	/**
	 * Transform the String format to Bytes to be send by output stream
	 * @return
	 */
	public byte[] bitFieldToBytes(String str) {
		byte[] bytes = this.bitField.getBytes();
		return bytes;
	}

	public byte[] toBytes(){
		byte[] bytes = this.toString().getBytes();
		return bytes;
	}

	/**
	 * Transform the integer format to Bytes to be send by output stream
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
		if(typeEncode(this.type) <= 4){
			// 1 - 4
			lengthByte = convertIntToByte(1, 4);
			return combineBytes(lengthByte, typeByte);
		} else if(typeEncode(this.type) == 5 || typeEncode(this.type) == 7){
			// 5 and 7
			lengthByte = convertIntToByte(5, 4);
			byte[] lengthAndType = combineBytes(lengthByte, typeByte);
			byte[] indexByte = convertIntToByte(this.index, 4);
			return combineBytes(lengthAndType, indexByte);
		} else if(typeEncode(this.type) == 8){
			//8
			byte[] payloadByte = this.payload;
			lengthByte = convertIntToByte(5 + payloadByte.length, 4);
			byte[] lengthAndType = combineBytes(lengthByte, typeByte);
			byte[] indexByte = convertIntToByte(this.index, 4);
			byte[] indexAndPayload = combineBytes(indexByte, payloadByte);
			return combineBytes(lengthAndType, indexAndPayload);
		} else{
			//6 bitfield as string, combine with length and type
			byte[] payloadByte = bitFieldToBytes(bitField);
			lengthByte = convertIntToByte(5 + payloadByte.length, 4);
			return combineBytes(lengthByte, typeByte);
		}

	}


	//transfer bytes to P2PMessage and call handlers
	public static P2PMessage byteToMessage(byte[] realMessage){
		//check type of the message
		// 0 - 3 is the length of the message
		// 4 - 7 is the type of the message
		byte[] curTypeByte = Arrays.copyOfRange(realMessage, 4, 8);
		int curTypeInt = convertByteToInt(curTypeByte);

		//TODO check the type is valid, range from 1 - 8
		P2PMessage receivedMessage = new P2PMessage();
		if(curTypeInt == 1){
			//receive chock
		}else if (curTypeInt == 2){
			//receive unchocked
		}else if (curTypeInt == 3){
			// receive interested
//			P2PMessageHandler.receiveInterested();
		}else if (curTypeInt == 4){
			// receive not interested
		}else if (curTypeInt == 5){
			// receive have message
		}else if (curTypeInt == 6){
			// receive bitfield
		}else if (curTypeInt == 7){
			//receive request message
		}else{
			//receive piece
		}
		return receivedMessage;
	}



	//combine two byte array as one
	public byte[] combineBytes(byte[] b1, byte[] b2){
		byte[] combined = new byte[b1.length + b2.length];
		for(int i = 0; i < combined.length; i++){
			combined[i] = i < b1.length ? b1[i] : b2[i - b1.length];
		}
		return combined;
	}



	public static void main(String[] args) {//Empty
	}
}

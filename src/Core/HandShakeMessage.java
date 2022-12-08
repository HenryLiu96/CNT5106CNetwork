package Core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *Check Project Description - Version 1 ->Protocol Description -> handshake message  
 *
 */
public class HandShakeMessage {
	private String msg;
	private String header;
	private String peerID;
	
	private static int length = 32;
	
	private final static String HANDSHAKE_PAT = "P2PFILESHARINGPROJ"
			+ "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000"
			+ "%s";
	private final static String ERR_MSG_BYTELEN = "Cannot compose ";
	
	
	//Constructor
	public HandShakeMessage(String handshakeReq) {
		// TODO Implement RegEx format check and initialize the fields
		//     You can see the Util.BinaryEncodingTool to get a flavor
		//     of this handShank message
		this.msg = handshakeReq;
		this.header = msg.substring(0,18);
		this.peerID = msg.substring(28);
	}
	
	public HandShakeMessage(byte[] handShakeBytes) throws Exception {
		if (handShakeBytes.length == length) {
			String handShake = new String(handShakeBytes);
			this.msg = handShake;
			this.header = msg.substring(0,18);
			this.peerID = msg.substring(28);
		}
		else {
			throw new Exception(ERR_MSG_BYTELEN);
		}
	}
	
	public HandShakeMessage(int peerID) {
		this.peerID = String.valueOf(peerID);
		this.msg = String.format(HANDSHAKE_PAT, this.peerID);
	}
	
	public static int getLength() {
		return length;
	}
	
	public boolean headerCheck() {
		// TODO Implement header Check,
		//		It is required to be exact string pattern
		Pattern p = Pattern.compile("P2PFILESHARINGPROJ");
		Matcher m = p.matcher(header);
		return m.matches();
	}
	
	public boolean peerIDCheck() {
		// TODO Implement peerID Check 
		//		It is required to be integer
		Pattern p = Pattern.compile("\\d{4}");
		Matcher m = p.matcher(peerID);
		return m.matches();
	}
	
	public boolean permitToConnect() {
		return headerCheck() && peerIDCheck();
	}
	
	public String toString() {
		return this.msg;
	}
	

	public static void main(String[] args) throws Exception {
		HandShakeMessage hm = new HandShakeMessage("P2PFILESHARINGPROJ\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u00001024");
		byte[] handshakeBytes = {'P','2','P','F','I','L','E','S','H','A','R','I'
				,'N','G','P','R','O','J','\u0000','\u0000','\u0000','\u0000',
				'\u0000','\u0000','\u0000','\u0000','\u0000','\u0000',
				'1','0','2','4'};
		
		System.out.println(hm.peerIDCheck());
		System.out.println("Fuck final test");
		System.out.println(new HandShakeMessage(handshakeBytes).msg);
	}
}

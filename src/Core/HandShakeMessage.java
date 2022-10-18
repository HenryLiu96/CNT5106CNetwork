package Core;
/**
 *Check Project Description - Version 1 ->Protocol Description -> handshake message  
 *
 */
public class HandShakeMessage {
	String msg;
	String header;
	String peerID;
	
	//Constructor
	public HandShakeMessage(String handshakeReq) {
		// TODO Implement RegEx format check and initialize the fields
		//     You can see the Util.BinaryEncodingTool to get a flavor
		//     of this handShank message
	}
	
	public boolean headerCheck() {
		// TODO Implement header Check,
		//		It is required to be exact string pattern
		return true;
	}
	
	public boolean peerIDCheck() {
		// TODO Implement peerID Check 
		//		It is required to be integer
		return true;
	}
	
	public boolean permitToConnect() {
		return headerCheck() && peerIDCheck();
	}
	
}

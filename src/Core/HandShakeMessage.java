package Core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		this.msg = handshakeReq;
		this.header = msg.substring(0,18);
		this.peerID = msg.substring(28);
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

	public static void main(String[] args) {
		HandShakeMessage hm = new HandShakeMessage("P2PFILESHARINGPROJ\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u00001024");
		System.out.println(hm.peerIDCheck());
	}
}

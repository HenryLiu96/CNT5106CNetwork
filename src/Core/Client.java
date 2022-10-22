package Core;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Paths;

import Util.Config;

public class Client {
	private static boolean VERBOSE = false;
	
	Socket requestSocket;
	P2PLogger logger;
	// TODO Fixed value 6001 for early stage testing, switch to read 
	// configuration file to get later
	private int port = 6001; 
	
	//Constants
	// Default relative configuration directory
	private static final String CONFIG_DIR = "\\Config";
	// Default relative configuration file name for PeerInfo
	private static final String PEER_INFO = "\\PeerInfo.cfg";
	// Number of fields in a peerInfo record besides the key
	private static final int FIELD_NUM = 3;
	
	//Error message
	private static final String ERR_CONFIG_LOAD_FAILURE = 
										"Failure to load configuration file.";
	private static final String ERR_UNKNOWN_PEER = 
						 "Input peer is not found in the configuration file.";
	private static final String ERR_WRONG_FIELD_NUM = 
			 "Incorrect field number retrieved is. Please check peerInfo.cfg";
	private static final String ERR_HOST_COMPONET_FAILURE = 
			                     "Failure in retrieving the host components.";
	private static final String ERR_ILLEGAL_PORT_NUM =
									   "The extracted port value is illegal!";
	
	/**
	 * Constructor
	 * 
	 * Input a peerID and send request out to
	 * setup a connection to the corresponding 
	 * host of the peerID.
	 * 
	 * @param peerID
	 * @throws Exception
	 */
	public Client(String peerID) throws Exception {
		logger = new P2PLogger();
		// Construct configuration file path to PeerInfo.cfg
		String workingDir = Paths.get("").toAbsolutePath().toString();
		String cfgPath = workingDir.concat(CONFIG_DIR).concat(PEER_INFO);
		
		// Load properties
		Config config = null;
		try 
		{
			config = new Config(cfgPath);
		} 
		catch(Exception e) {
			e.printStackTrace();
			logger.logError(e);
		}
		
		// If failed to load the configuration file, throw exception and abort
		if (config==null) {
			Exception e = new Exception(ERR_CONFIG_LOAD_FAILURE);
			logger.logError(e);
			
			throw e;
		}
		
		// If failed to find a corresponding peer in the configuration file
		// throw exception and abort
		if(!config.containsKey(peerID)) {
			Exception e = new Exception(ERR_UNKNOWN_PEER);
			logger.logError(e);
			
			throw e;
		}
		
		// Retrieve PeerInfo
		String peerInfo = config.getString(peerID);
		// Split the string to fetch three fields
		String[] infoFields = peerInfo.split(" ");
		
		// Check the number of fields extracted
		// Correct field number should be 3, if not, throw exception and abort
		checkFieldNum(infoFields);
		
		// Load and check for host name validity
		String peerHostName = infoFields[1];
		//checkValidHostName(peerHostName); TODO release this on final test
		
		//load port number and check for validity
		String portNum = infoFields[2];
		port = Integer.getInteger(portNum);
		checkValidPort(port);
		
		String IDKWTF = infoFields[3];// TODO check what this fields later
		//TODO check this field also.
		
		//Do field checks
		
		try {
			// Create a socket to host with host name and port number
			this.requestSocket = new Socket(peerHostName, port);
		} catch (UnknownHostException e) {
			logger.logError(e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.logError(e);
			e.printStackTrace();
		}
	}
	
	/**
	 * Check whether the fields extracted satisfy the expected 
	 * fields of a peerInfo record.
	 * If not, throw exception and abort.
	 * 
	 * @param infoFields
	 * @throws Exception
	 */
	private void checkFieldNum(String[] infoFields) throws Exception {
		if(infoFields.length != FIELD_NUM) {
			Exception wrong_field_numException = new Exception(ERR_WRONG_FIELD_NUM);
			logger.logError(wrong_field_numException);
			
			throw wrong_field_numException;
		}
	}
	
	/**
	 * Check whether the host is in right syntax 
	 * and if the host components cannot be retrieved,
	 * throw exception and abort.
	 * 
	 * @param hostName
	 * @throws URISyntaxException
	 */
	private void checkValidHostName(String hostName) throws URISyntaxException {
		    URI uri = new URI(hostName);
		    if (uri.getHost() == null) {
		    	throw new URISyntaxException(ERR_HOST_COMPONET_FAILURE, hostName);
		    }

	}
	
	/**
	 * Check whether the extracted port number value is in legal range
	 * if not, throw exception and abort.
	 * 
	 * @param port
	 * @throws Exception
	 */
	private void checkValidPort(int port) throws Exception {
		if(port>=0 && port<=65536) 
		{
			if(VERBOSE) 
				System.out.println(String.format(
						"Extracted port field value [%d] is in legal range",
						 port)
					);
		} else {
			throw new Exception(ERR_ILLEGAL_PORT_NUM);
		}
	}
	
	public static void main(String[] args) throws Exception {
		String workingDir = Paths.get("").toAbsolutePath().toString();
		String cfgPath = workingDir.concat(CONFIG_DIR).concat(PEER_INFO);
		Config config = new Config(cfgPath);
		config.showMenu();
		String peerID1 = "1001";
		String peerID2 = "1002";
		String peerID7 = "1007";
		System.out.println(config.getString(peerID1));
		System.out.println(config.containsKey(peerID2));
		System.out.println(config.containsKey(peerID7));
		System.out.println(config.getString(peerID1).split(" ")[0]);
		System.out.println(config.getString(peerID1).split(" ")[1]);
		System.out.println(config.getString(peerID1).split(" ")[2]);
		
		
	}
}

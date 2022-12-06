package Core;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Paths;

import Util.Config;

public class Client {
	private static boolean VERBOSE = false;
	
	private Socket requestSocket;
	private P2PLogger logger;
	
	//Declare output and input streams
	private OutputStream reqOut = null;
	private ObjectOutputStream objOut = null;
	private InputStream reqIn = null;
	private ObjectInputStream objIn = null;
	
	// TODO Fixed value 6001 for early stage testing, switch to read 
	// configuration file to get later
	private int port = 6001;


	//NEW ADDING -- Liu
	public int peerID;
	
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
	private static final String ERR_STREAM_NOT_READY = 
										   "Connection Streams are not ready";
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
		this.peerID = Integer.parseInt(peerID);
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
		
		//TODO check this field also.
		String IDKWTF = infoFields[3];// TODO check what this fields later
		
		try {
			// Create a socket to host with host name and port number
			this.requestSocket = new Socket(peerHostName, port);
			// Log attempt to establish connection to host
			logger.append(String.format("Attempt to connect [%s] at port[%d] "
					                				, peerHostName, portNum));
			logger.log();
		}
		catch (UnknownHostException e) {
			logger.logError(e);
			e.printStackTrace();
		} 
		catch (IOException e) {
			logger.logError(e);
			e.printStackTrace();
		}
		
		try {
			// Initialize output streams
			reqOut = requestSocket.getOutputStream();
			objOut = new ObjectOutputStream(reqOut);
			
			//Write object to the server welcome socket
			objOut.flush();
			
			// Initialize input streams
			reqIn = requestSocket.getInputStream();
			objIn = new ObjectInputStream(reqIn);	
		}catch(Exception e) {
			logger.logError(e);
			e.printStackTrace();
			throw e;
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
	@SuppressWarnings("unused") //TODO release when final test
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
	
	/**
	 * Close all the streams after everything is done.
	 *
	 * @throws IOException 
	 * @throws Exception
	 */
	public void killAllStreams() throws IOException {
			if(objOut != null) {
				objOut.close();
			}
			if(reqOut != null) {
				reqOut.close();
			}
			if(objIn != null) {
				objIn.close();
			}
			if(reqIn != null) {
				reqIn.close();
			}
	}
	
	/**
	 * Send byte stream to the server
	 * @param msg
	 * @throws Exception
	 */
	public void messageServer(P2PMessage msg) throws Exception {
		// If any IO streams closed or not initialized, throw exception 
		if(objOut==null
				||reqOut == null
				||objIn  == null 
				||reqIn  == null ) 
		{
			throw new Exception(ERR_STREAM_NOT_READY);
		}
		this.objOut.write(msg.toBytes());
	}
	
	/**
	 * Encode the input stream to String instance
	 * These instances can be encapsulated into P2PMessage for further processing
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public String extractMessage() throws ClassNotFoundException, IOException {
		String message = (String)objIn.readObject();
		return message;
	}

	public void sendMessage(byte[] realMessage, int peerID){

	}

	public void receiveMessage(byte[] realMessage, int peerID){


	}




	
	/**
	 * Test for load configuration file and key set showing
	 */
	private static void test1() {
		String workingDir = Paths.get("").toAbsolutePath().toString();
		String cfgPath = workingDir.concat(CONFIG_DIR).concat(PEER_INFO);
		Config config = new Config(cfgPath);
		config.showMenu();
	}
	
	/**
	 * Test for peerInfo field extracting
	 * @throws Exception
	 */
	private static void test2() throws Exception {
		String workingDir = Paths.get("").toAbsolutePath().toString();
		String cfgPath = workingDir.concat(CONFIG_DIR).concat(PEER_INFO);
		Config config = new Config(cfgPath);
		String peerID1 = "1001";
		String peerID2 = "1002";
		String peerID7 = "1007";
		System.out.println(config.getString(peerID1));
		System.out.println(config.containsKey(peerID2));
		System.out.println(config.containsKey(peerID7));
		System.out.println("HostName:"+config.getString(peerID1).split(" ")[0]);
		System.out.println("Port："+config.getString(peerID1).split(" ")[1]);
		System.out.println("?:"+config.getString(peerID1).split(" ")[2]);
	}
	
	public static void main(String[] args) throws Exception {
		test1();
		test2();
	}
}

package Core;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import Util.Config;
import Util.Logger;

public class P2PLogger extends Logger {
	private boolean VERBOSE = true;
	
	private String fileDir;
	
	// Default relative logging directory.
	private static final String DEFAULT_DIR = "\\P2P\\Logging";
	// Default relative configuration directory
	private static final String CONFIG_DIR = "\\Config";
	// Default configuration file name
	private static final String LOGGER_CONFIG = "\\Logger.config";
	
	//Error message
//	private static final String CONFIG_LOAD_FAILURE = "Cannot load assigned"
//													+ " configuration file!";

	/**
	 * Construction override
	 * @throws Exception
	 */
	public P2PLogger() throws Exception {
		// Get absolute path from where where the class is run from.
		String workingDir = Paths.get("").toAbsolutePath().toString();
		// Construct the path to configuration file
		String cfgPath = workingDir.concat(CONFIG_DIR).concat(LOGGER_CONFIG);
		Config cfg = null;
		// A robust configuration setup
		try {
			// If configuration file is available
			cfg = new Config(cfgPath);
			this.fileDir = workingDir.concat(cfg.getString("LOG_DIR"));
			this.setfilePath(this.fileDir);
			if(VERBOSE) 
				System.out.println("P2P Logger initialized with configuration file!");
		}catch(Exception e) {
			// If configuration file is not available
			this.fileDir = workingDir+DEFAULT_DIR;
			this.setfilePath(this.fileDir);
			if(VERBOSE) 
				System.out.println("P2P Logger initialized with default setup!");
		}
		
		File fDir = new File(this.fileDir);
		// If this file path don't exist corresponding directory, create dir.
		if(!fDir.exists() || !fDir.isDirectory()) {
			if(VERBOSE) 
				System.out.println("Directory not found, create dir!");
			fDir.mkdirs();
		}
	}
	
	/**
	 * Set PeerID as prefix
	 * @param peerID
	 */
	public void setPeerInfo(String peerID) {
		//TODO check if peerID is in legal form
		this.setPrefix(peerID);
	}


	//TODO add message to log
	public void addMessage(int peerID, String message){

	}

	public static void main(String[] args) throws Exception {
//		List<Integer> a = new ArrayList<>();
		String workingDir = Paths.get("").toAbsolutePath().toString();
		String cfgDir = workingDir.concat(CONFIG_DIR);
		
		Config cfg = new Config(cfgDir.concat(LOGGER_CONFIG));
		cfg.showMenu();
		System.out.println();
		
		P2PLogger pl = new P2PLogger();
		pl.setPeerInfo("10086");
		pl.append("HooooooooWeeeeeeeeee");
		pl.log();	
	}
}

package Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	// Legal suffix
	private final String CFG = ".cfg";
	private final String CONFIG = ".config";
	
	// Error messages
	private final String ERR_INITIALIZATION = "Illegal Operation: Configuration "
														+ "is not initialized!";
	private final String ERR_SUFFIX = "Illegal config file suffix.";
	private final String ERR_KEY_NOT_FOUND = "Property of [%s] is not included "
														+ "in config.";
	private final String ERR_FILE_NOT_FOUND = "Assigned Configuration File is "
														+ "not found!";
	
	// To note if the class is feed with a valid file.
	boolean initialized = false;
	Properties prop;
	
	/**
	 * To avoid empty constructor being invoked.
	 */
	@SuppressWarnings("unused")
	private Config() {}
	
	/**
	 * Constructor
	 * @param configFilePath
	 */
	public Config(String configFilePath){
		try {
			// To examine if target is legal configuration file
			if (!configFilePath.endsWith(CFG) &&
				!configFilePath.endsWith(CONFIG)) {
				throw new IOException(ERR_SUFFIX);
			}
			this.prop = new Properties();
			File f = new File(configFilePath);
			
			// To examine if target is a solid file
			if(f.exists() && f.isFile()) {
				FileInputStream fis = new FileInputStream(f);
				prop.load(fis);
				initialized = true;
			} else {
				throw new Exception(ERR_FILE_NOT_FOUND);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Input property name, return integer.
	 * @param key
	 * @throws Exception
	 */
	public int getInt(String key) throws Exception {
		// If the config file is not read, throw exception
		if(!initialized) {
			throw new Exception(ERR_INITIALIZATION);
		}
		try {
			// If this property is contained in configuration file
			if(this.prop.containsKey(key)) {
				return Integer.valueOf(this.prop.getProperty(key));
			}
			// If this property is not contained, throw error
			else {
				throw new Exception(String.format(ERR_KEY_NOT_FOUND, key));
			}
		}catch(NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		throw new Exception(
				String.format("Cannot get integer with key[%s].", key));
	}
	
	/**
	 * Input property key, return String.
	 * @param key
	 * @throws Exception
	 */
	public String getString(String key) throws Exception {
		if(!initialized) {
			throw new Exception(ERR_INITIALIZATION);
		}
		try {
			// If this property is contained in configuration file
			if(this.prop.containsKey(key)) {
				return this.prop.getProperty(key);
			}
			// If this property is not contained, throw error
			else {
				throw new Exception(String.format(ERR_KEY_NOT_FOUND, key));
			}
		}catch(NumberFormatException nfe) {
			nfe.printStackTrace();
		}
		throw new Exception(
				String.format("Cannot get String with key[%s].", key));
	}
	
	public static void main(String[] args) throws Exception {
		Config c = new Config("C:\\Users\\Tianhui Liu\\eclipse-workspace"
							+ "\\CNT5106CNetwork\\src\\Core\\common.cfg");
		for(Object O: c.prop.keySet()) {
			System.out.println((String)O);
		}
		System.out.println(c.getInt("PieceSize"));
		System.out.println(c.getString("Hime"));
	}
}

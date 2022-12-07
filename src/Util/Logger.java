package Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger{
	// Variable for printing for debugging
	boolean VERBOSE = true;
	// Default relative logging directory.
	private static final String DEFAULT_DIR = "\\Logging";
	// Format for logger line time presenting.
	private static final String DEFAULT_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
	// Format for logger file name date.(Easier to use to create a log every day) 
	private static final String DEFAULT_DATE_FORMAT = "dd_MM_yyyy";
	// Log file suffix.
	private static final String LOG_SUFFIX = ".log";
	
	//Error message
	private static final String ERR_FAILURE_CREATE_LOG = "Failure: log file "
													+ "cannot be created.";
	
	//Properties from configuration file
	private String fileDir;
	private StringBuffer sb;
	private String prefix = "";
	
	/**
	 * Default constructor
	 */
	public Logger() {
		// Get absolute path from where where the class is run from.
		String workingDir = Paths.get("").toAbsolutePath().toString();
		this.fileDir = workingDir+DEFAULT_DIR;
		File fDir = new File(this.fileDir);
		// If this file path don't exist corresponding directory, create dir.
		if(!fDir.exists() || !fDir.isDirectory()) {
			System.out.println("Directory not found, create dir!");
			fDir.mkdirs();
		}
		sb = new StringBuffer();
	}
	
	/**
	 * Alternative constructor with designated log file directory
	 * @param fileDir
	 */
	public Logger(String fileDir) {
		this.fileDir = fileDir;
		File fDir = new File(DEFAULT_DIR);
		if(!fDir.exists() ||! fDir.isDirectory()) {
			System.out.println("Directory not found, create dir!");
			fDir.mkdirs();
		} 
		sb = new StringBuffer();
	}
	
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public void setfilePath(String fileDir) {
		this.fileDir = fileDir;
	}
	
	/**
	 * Write everything in current String buffer to log file
	 * @throws Exception
	 */
	public void log() throws Exception {
		// Get current time.
		LocalDateTime now = LocalDateTime.now(); 
		
		// Format current date as log file name.
		DateTimeFormatter dFormat = DateTimeFormatter
					.ofPattern(DEFAULT_DATE_FORMAT);
		String logFile = dFormat.format(now);
		String logPath = this.fileDir +"\\Log" + logFile + LOG_SUFFIX;
		
		//Check if log is already created or if it is not a file.
		File log = new File(logPath);
		boolean fileExist = log.exists() 
						 && log.isFile();
		if(!fileExist) {
			try {
				// If not exist, try create new log file.
				fileExist = log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// If the log file is not successfully created.
		if (!fileExist) {
			throw new Exception(ERR_FAILURE_CREATE_LOG);
		}
		// Use a buffered writer to append the content to log file.
		FileWriter fWriter = null;
		BufferedWriter bWriter = null;
		PrintWriter pWriter = null;
		try {
			// This line is for developer debugging only.
			if (VERBOSE) System.out.println(sb.toString());
			fWriter = new FileWriter(logPath, true);
		    bWriter = new BufferedWriter(fWriter);
		    pWriter = new PrintWriter(bWriter);
		    pWriter.append(sb);
		    
		    // After logging, clear the string buffer.
		    this.clear();
		} catch(IOException e){
			e.printStackTrace();
		}finally {
			// Make sure buffered writer is closed if initialized ever.
		    if (pWriter != null) {
		    	pWriter.close();
		    }
		    if (bWriter != null) {
		    	bWriter.close();
		    }
		    if (fWriter != null) {
		    	fWriter.close();
		    }
		}
	}
	
	/**
	 * Append a string and current day time to the buffer
	 * @param s
	 */
	public void append(String s) {
		// Format the dayTime to configured format
		LocalDateTime now = LocalDateTime.now();  
		DateTimeFormatter dtFormat = DateTimeFormatter
									.ofPattern(DEFAULT_TIME_FORMAT);
		String dayTime = dtFormat.format(now);
		
		// If peer Information is given, also append to the buffer
		if(!this.prefix.isBlank()) {
			this.sb.append(String.format("Peer: [%s]", prefix));
		}
		
		//Make log file like[21/10/2022 17:40:35] Info: Here is a example log
		this.sb.append(String.format("[%s]: ", dayTime));
		this.sb.append(s+"\n");
	}
	
	/**
	 * Append error message to String buffer.
	 * @param e
	 * @throws Exception 
	 */
	public void logError(Exception e) throws Exception {
		// Format the dayTime to configured format.
		LocalDateTime now = LocalDateTime.now();  
		DateTimeFormatter dtFormat = DateTimeFormatter
									.ofPattern(DEFAULT_TIME_FORMAT);
		String dayTime = dtFormat.format(now);
		
		// If peer Information is given, also append to the buffer
		if(!this.prefix.isBlank()) {
			this.sb.append(String.format("[%s]", prefix));
		}
		
		// Make log file like[21/10/2022 17:40:35] ERROR: Here is a example log.
		this.sb.append(String.format("[%s] ERROR: ", dayTime));
		
		// Append detailed error message to the String buffer.
		String s = e.getMessage();
		this.sb.append(s+"\n");
		this.log();
	}
	
	/**
	 * Clear the content in the StringBuffer
	 */
	public void clear() {
		this.sb.delete(0, sb.length());
	}

	public static void main(String[] args) throws Exception {
		LocalDateTime now = LocalDateTime.now();  
		DateTimeFormatter dtFormat = DateTimeFormatter
									.ofPattern(DEFAULT_TIME_FORMAT);
		DateTimeFormatter dFormat = DateTimeFormatter
									.ofPattern(DEFAULT_DATE_FORMAT);
		System.out.println(dtFormat.format(now));
		System.out.println(dFormat.format(now));
		
		Logger l = new Logger();
		l.setPrefix("ALOHA PEER!");
		l.append("Dameyo, dame dame!");
		l.append("Hawaii guitar");
		l.log();
		l.append("This should be single line!");
		l.log();
	}
}

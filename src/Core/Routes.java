package Core;

import java.nio.file.Paths;

/**
 * Class for public route sharing 
 * @author Tianhui Liu
 *
 */
public class Routes {
	public static final String CONFIG_DIR = "\\Config";
	public  static final String COMMON_CFG = "\\common.cfg";
	
	public static void main(String[] args) {
		System.out.println(CONFIG_DIR.concat(COMMON_CFG));
		
	}
}

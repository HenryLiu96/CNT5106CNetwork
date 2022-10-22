package Util;

import java.util.Arrays;

/**
 * A tool for binary data encoding
 * @author Tianhui Liu
 */
public class BinaryEncodingTool {
	private String output;
	/**
	 * Constructor 
	 * @param input e.g. "01110100" will get you a "t"
	 */
	public BinaryEncodingTool(String input) {
		StringBuilder sb = new StringBuilder(); 
		//Split the input by every 8 bits
		Arrays.stream(input.split("(?<=\\G.{8})")
		)//Iterate over all the 8 bit pieces
		.forEach(
			// Encode the 8-bit pieces and append to string builder
			s -> sb.append((char) Integer.parseInt(s, 2)) 
		);
		
		//Output the 
		String output = sb.toString(); 
		this.output = output;
	}
	
	/**
	 * Return encoded string
	 */
	public String write() {
		return output;
	}
	
	/**
	 * A test example
	 */
	public static void tryHandShankMsg() {
		// This is a sample for our handshake message
		String binaryString = "0101000000110010"
							+ "0101000001000110"
							+ "0100100101001100"
							+ "0100010101010011"
							+ "0100100001000001"
							+ "0101001001001001"
							+ "0100111001000111"
							+ "0101000001010010"
							+ "0100111101001010"
							+ "0000000000000000"
							+ "0000000000000000"
							+ "0000000000000000"
							+ "0000000000000000"
							+ "0000000000000000"
							+ "0011000100110000"
							+ "0011001000110100"; 
		BinaryEncodingTool bet = new BinaryEncodingTool(binaryString);
		System.out.println(String.format("Encoded String: %s", bet.write()));
	}
	
	public static void main(String[] args) {
		tryHandShankMsg();
	}
}

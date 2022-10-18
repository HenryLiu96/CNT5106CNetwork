package Util;

import java.util.Arrays;

public class BinaryEncodingTool {
	String output;
	/**
	 * 
	 * @param input e.g. "01110100" will get you a "t"
	 */
	public BinaryEncodingTool(String input) {
		StringBuilder sb = new StringBuilder(); // Some place to store the chars

		Arrays.stream(input.split("(?<=\\G.{8})") // Splits the input string into 8-char-sections (Since a char has 8 bits = 1 byte)
		).forEach(s -> // Go through each 8-char-section...
		    sb.append((char) Integer.parseInt(s, 2)) // ...and turn it into an int and then to a char
		);

		String output = sb.toString(); // Output text (t)
		this.output = output;
	}
	
	public String write() {
		return output;
	}
	
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

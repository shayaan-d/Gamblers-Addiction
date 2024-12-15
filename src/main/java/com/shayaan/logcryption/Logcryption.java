package com.shayaan.logcryption;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to "encrypt" data using the Cantor pairing function. <br>
 * Security? None. <br>
 * Coolness? 0. <br>
 * Next to no storage? I guess.
 * @see <a href="https://en.wikipedia.org/wiki/Pairing_function">https://en.wikipedia.org/wiki/Pairing_function</a>
 * @author shayaan
 */
public final class Logcryption {


	private Logcryption() {

	}

	/**
	 * Implement the Cantor pairing function for two chars.
	 * @param a Argument for pairing function. Order matters.
	 * @param b Argument for pairing function. Order matters.
	 * @return A single character representative of both characters inputted, can be reversed using <code>unpairing(c)</code>
	 */
	private static char pairing(char a, char b) {
		// f(m, n) = (m + n)(m + n + 1) / 2 + n

		int prod = ((int) a + (int) b) * ((int) a + (int) b + 1);
		return (char) (prod/2 + (int) b);
	}

	/**
	 * The inverse of the Cantor pairing function.
	 * @param c A character that represents two others.
	 * @return The characters provided through the inverse of the Cantor pairing function.
	 */
	private static char[] unpairing(char c) {
		int w = (int) ((Math.sqrt((double) 8 * c + 1) - 1) / 2);
		int t = (int) ((Math.pow(w, 2) + w) / 2);
		char y = (char) (c - t);
		char x = (char) (w - y);
		return new char[]{x, y};
	}

	/**
	 * Splits a string into two-character length substings
	 * @param str The string to split.
	 * @return A list of two-character or fewer strings. Only the last string will ever have a length less than two.
	 */
	private static List<String> splitString(String str) {
		List<String> result = new ArrayList<>();
		for (int i = 0; i < str.length(); i += 2) {
			result.add(str.substring(i, Math.min(i + 2, str.length())));
		}
		return result;
	}

	/**
	 * Encodes a string using Logcryption. Does this by splitting, pairing, and recombining the string.
	 * @param string The string to encode.
	 * @return An encoded string.
	 */

	public static String encode(String string) {
		List<String> list = splitString(string);
		StringBuilder builder = new StringBuilder();
		for (String str : list) {
			if (str.length() < 2) {
				builder.append(pairing(str.charAt(0), '\u0000'));
			} else {
				builder.append(pairing(str.charAt(0), str.charAt(1)));
			}
		}
		return builder.toString();
	}

	/**
	 * Decodes a string that has been encrypted by Logcryption. Decombines, depairs, and refuses the string.
	 * @param string The encoded string.
	 * @return The decoded string.
	 */
	public static String decode(String string) {
		StringBuilder builder = new StringBuilder();
		for (char c : string.toCharArray()) {
			char[] chars = unpairing(c);
			builder.append(chars[0]);
			if (chars[1] != '\u0000') {
				builder.append(chars[1]);
			}
		}
		return builder.toString();
	}
}

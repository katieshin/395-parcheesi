package parcheesi;

public class Utils {
	/**
	 * Converts a primitive integer to a primitive float.
	 *
	 * @param num The integer to convert to float
	 *
	 * @return Float value
	 */
	public static float int2float(int num) {
		return (new Integer(num)).floatValue();
	}

	/**
	 * Repeats a string a specified number of times.
	 *
	 * @param times The number of times to repeat the string
	 * @param s The string to repeat
	 *
	 * @return Repeated string
	 */
	public static String repeatString(int times, String s) {
		String result = "";

		for (int i = 0; i < times; i++) {
			result += s;
		}

		return result;
	}

	/**
	 * Translates a PascalCased string into kebab-case.
	 *
	 * @param s String to convert to kebab-case
	 *
	 * @return Kebab-cased string
	 */
	public static String dashify(String s) {
		String result = "";
		char[] chars = s.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (Character.isUpperCase(c) && i != 0) {
				result += "-";
			}
			result += Character.toLowerCase(c);
		}

		return result;
	}
}

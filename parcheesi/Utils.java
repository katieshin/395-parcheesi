package parcheesi;

public class Utils {
	/**
	 * Converts a primitive integer to a primitive float.
	 *
	 * @param num The integer to convert to float
	 *
	 * @return float
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
	 * @return String
	 */
	public static String repeatString(int times, String s) {
		String result = "";

		for (int i = 0; i < times; i++) {
			result += s;
		}

		return result;
	}
}

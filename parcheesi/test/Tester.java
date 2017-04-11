package parcheesi.test;

import static parcheesi.Utils.*;

import java.util.LinkedList;

public class Tester {
	private int failures = 0;
	private int tests = 0;

	private LinkedList<String> failureSummaries = new LinkedList<String>();

	/**
	 * Checks if an expression is true. If so, print a success message. If not,
	 * track the failure and print a failure message.
	 *
	 * @param test The result of the expression to check
	 * @param description A description of the expression whose result is {@code test}
	 */
	protected void check(boolean test, String description) {
		String result = "";

		tests++;

		if (test) {
			result += "✓ " + description;
		} else {
			failures++;
			result += "✗ " + description;
			failureSummaries.add(result);
		}

		System.out.println(result);
	}

	/**
	 * Summarizes checks made so far.
	 */
	protected void summarize() {
		if (failures == 0) {
			System.out.println("No failed tests");
			return;
		}

		for (String failureSummary : failureSummaries) {
			System.out.println(failureSummary);
		}

		System.out.println(repeatString(8, "=========="));

		float percentageFailed = int2float(failures) / int2float(tests);
		System.out.println("Percentage of tests failed: " + percentageFailed + "%");
	}
}

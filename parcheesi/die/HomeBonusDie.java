package parcheesi.die;

import parcheesi.test.Tester;

public class HomeBonusDie extends Die {
	public HomeBonusDie() {
		this.value = 10;
	}

	public static void main (String[] args) {
		new HomeBonusDieTester();
	}

	static class HomeBonusDieTester extends Tester {
		public HomeBonusDieTester() {
			HomeBonusDie hbd = new HomeBonusDie();
			check(
				hbd.getValue() == 10,
				"HomeBonusDie should have a value of 10."
			);

			summarize();
		}
	}
}

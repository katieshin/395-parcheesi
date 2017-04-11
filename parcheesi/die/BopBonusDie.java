package parcheesi.die;

public class BopBonusDie extends Die {
	public BopBonusDie() {
		this.value = 20;
	}

	public static void main(String[] args) {
		new BopBonusDieTester();
	}

	static class BopBonusDieTester extends parcheesi.test.Tester {
		public BopBonusDieTester() {
			BopBonusDie bbd = new BopBonusDie();

			check(
				bbd.getValue() == 20,
				"BopBonusDie should have a value of 20."
			);

			summarize();
		}
	}
}

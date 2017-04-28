package parcheesi.die;

public class DoublesBonusDie extends NormalDie {
	public DoublesBonusDie(int value) throws Die.InvalidDieException {
		super(value);
	}

	public static void main(String[] args) throws Die.InvalidDieException {
		// NOTE: MUST pass in a die with value 4. (Assumption made by tester.)
		new DoublesBonusDieTester(new DoublesBonusDie(4));
	}

	static class DoublesBonusDieTester extends NormalDie.NormalDieTester<DoublesBonusDie> {
		public DoublesBonusDieTester(DoublesBonusDie validDbd)
			throws Die.InvalidDieException {
			super(validDbd);
		}
	}
}

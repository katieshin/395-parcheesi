package parcheesi.die;

import java.lang.reflect.InvocationTargetException;

public class DoublesBonusDie extends NormalDie {
	public DoublesBonusDie(int value) throws Die.InvalidDieException {
		super(value);
	}

	/* While the following seems lengthy, it actually just re-runs NormalDie's tests replacing the
	 * generic type parameter with DoubleBonusDie instead (because DoubleBonusDie is a derivative of
	 * NormalDie).
	 */
	public static void main(String[] args)
		throws Die.InvalidDieException {
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

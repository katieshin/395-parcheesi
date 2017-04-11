package parcheesi.die;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class NormalDie extends Die {
	public NormalDie(int value) throws InvalidDieException {
		if (value >= 1 && value <= 6) {
			this.value = value;
		} else {
			// NOTE: Game will implode if invalid die roll is passed to constructor.
			// FIXME: May not be the best UX?
			throw new Die.InvalidDieException(value);
		}
	}

	// What follows is... slightly unsightly. You have been warned.
	public static void main(String[] args)
		throws	InstantiationException,
				NoSuchMethodException,
				IllegalAccessException,
				InvocationTargetException,
				// ^^^ Reflection Exceptions. Yup.
				Die.InvalidDieException {
		// NOTE: MUST pass in a die with value 4. (Assumption made by tester.)
		new NormalDieTester<NormalDie>(new NormalDie(4));
	}

	protected static class NormalDieTester<T extends NormalDie> extends parcheesi.test.Tester {
		public NormalDieTester (T valid)
			throws	InstantiationException,
					NoSuchMethodException,
					IllegalAccessException,
					InvocationTargetException,
					// ^^^ Reflection Exceptions. Yup.
					Die.InvalidDieException {
			String dieTypeName = valid.getClass().getSimpleName();

			check(
				valid.getValue() == 4,
				dieTypeName + " stores a value between 1 and 6 (inclusive)."
			);

			// Get the constructor for this type that takes 1 int as its only argument.
			Constructor<? extends NormalDie> ctor = valid.getClass().getConstructor(int.class);

			boolean ltInvalid = false;
			try {
				ctor.newInstance(0);
			} catch (InvocationTargetException ex) {
				if (ex.getCause() instanceof Die.InvalidDieException) {
					ltInvalid = true;
				}
			}
			check(ltInvalid, dieTypeName + " cannot have a value less than 1.");

			boolean gtInvalid = false;
			try {
				ctor.newInstance(100);
			} catch (InvocationTargetException ex) {
				if (ex.getCause() instanceof Die.InvalidDieException) {
					gtInvalid = true;
				}
			}
			check(gtInvalid, dieTypeName + " cannot have a value greater than 6.");

			summarize();
		}
	}
}

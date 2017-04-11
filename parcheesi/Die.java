package parcheesi;

public class Die {
	public class InvalidDieException extends Exception {
		public InvalidDieException(int value) {
			super("Given invalid die roll: " + String.valueOf(value));
		}
	}

	int value;

	public Die(int value) throws InvalidDieException {
		if (value >= 1 && value <= 6) {
			this.value = value;
		} else {
			// NOTE: Game will implode if invalid die roll is passed to constructor.
			// FIXME: May not be the best UX?
			throw new InvalidDieException(value);
		}
	}

	public int getValue() {
		return this.value;
	}
}

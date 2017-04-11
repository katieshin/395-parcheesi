package parcheesi;

public class Die {
	class InvalidDieException extends Exception { }

	int value;

	public Die(int value) {
		if (value >= 1 && value <= 6) {
			this.value = value;
		} else {
			// NOTE: Game will implode if invalid die roll is passed to constructor.
			// FIXME: May not be the best UX?
			throw InvalidDieException("Given invalid die roll: " + String.valueOf(value));
		}
	}

	public int getValue() {
		return this.value;
	}
}

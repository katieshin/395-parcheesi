package parcheesi.die;

public abstract class Die {
	public class InvalidDieException extends Exception {
		public InvalidDieException(int value) {
			super("Given invalid die roll: " + String.valueOf(value));
		}
	}

	protected int value;

	public int getValue() {
		return this.value;
	}
}

package parcheesi;

import parcheesi.Color;

// NOTE: Do not change this interface!
public class Pawn {
	int /* 0-3 */ id;
	String color;
	public Pawn (int id, String color) {
		this.id=id;
		this.color=color;
	}

	public Color getColor() {
		return Color.lookupByColorName(this.color);
	}
}

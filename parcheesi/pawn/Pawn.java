package parcheesi.pawn;

import parcheesi.Color;

public class Pawn {
	public int /* 0-3 */ id;
	public Color.Player color;

	public int playerIndex;

	public Pawn(int id, Color.Player color) {
		this.id = id;
		this.color = color;
		this.playerIndex = color.ordinal();
	}

	public Pawn(int id, String color) {
		this(id, Color.Player.lookupByColorName(color));
	}
}

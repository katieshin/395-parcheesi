package parcheesi.pawn;

import parcheesi.Color;

public class PawnWhisperer {
	public static Color.Player color(Pawn pawn) {
		return Color.Player.lookupByColorName(pawn.color);
	}

	public static int playerIndex(Pawn pawn) {
		return PawnWhisperer.color(pawn).ordinal();
	}

	public static int id(Pawn pawn) {
		return pawn.id;
	}
}

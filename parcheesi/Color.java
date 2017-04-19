package parcheesi;

public class Color {
	public interface ColorEnum {
		String getColorName();
		String getColorHex();
	}

	public enum Player implements ColorEnum {
		Player1("Red", "#FF0000"),
		Player2("Blue", "#0000FF"),
		Player3("Yellow", "#FFFF00"),
		Player4("Green", "#00FF00");
		// NOTE: In order to support more players, add more colors for them.
		
		private final String name;
		private final String hex;
		private Player(String name, String hex) {
			this.name = name;
			this.hex = hex;
		}

		public String getColorName() {
			return this.name;
		}

		public String getColorHex() {
			return this.hex;
		}

		public static Color.Player lookupByColorName(String name) {
			for (Color.Player c: Player.values()) {
				if (c.getColorName().equals(name)) return c;
			}

			return null;
		}
	}

	public enum Space implements ColorEnum {
		Neutral("LightBlue", "#AAAAFF"),
		Safe("Purple", "#CC00FF");

		private final String name;
		private final String hex;
		private Space(String name, String hex) {
			this.name = name;
			this.hex = hex;
		}

		public String getColorName() {
			return this.name;
		}

		public String getColorHex() {
			return this.hex;
		}
	}

	public static Color.Player forPlayer(int i) {
		return Color.Player.valueOf("Player" + (i + 1));
	}

	public static ColorEnum lookupByColorName(String name) {
		for (ColorEnum c: Player.values()) {
			if (c.getColorName().equals(name)) return c;
		}

		for (ColorEnum c: Space.values()) {
			if (c.getColorName().equals(name)) return c;
		}

		return null;
	}
}


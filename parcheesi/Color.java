package parcheesi;

public enum Color {
	Player1("Red", "#FF0000"),
	Player2("Blue", "#0000FF"),
	Player3("Yellow", "#FFFF00"),
	Player4("Green", "#00FF00"),
	// NOTE: In order to support more players, add more colors for them.
	Neutral("LightBlue", "#AAAAFF"),
	Safe("Purple", "#CC00FF");
	
	private final String name;
	private final String hex;
	private Color(String name, String hex) {
		this.name = name;
		this.hex = hex;
	}

	public String getColorName() {
		return this.name;
	}

	public String getColorHex() {
		return this.hex;
	}

	public static Color lookupByColorName(String name) {
		for (Color c: values()) {
			if (c.getColorName().equals(name)) return c;
		}

		return null;
	}
}


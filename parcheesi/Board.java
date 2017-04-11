package parcheesi;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.HashMap;

import parcheesi.player.Player;
import parcheesi.move.Move;
import parcheesi.Color;

public class Board {
	abstract class Location {
		Color[] colors;
		int index;

		public Location(Color[] colors, int index) {
			this.colors = colors;
			this.index  = index;
		}

		public int next(Pawn p) throws OperationNotSupportedException {
			return index + 1;
		};
	}

	/* NOTE: Although HomeRow is technically Safe, it doesn't need to extend Safe, because a pawn of
	 * a color dissimilar to that of the HomeRow locations cannot enter the HomeRow.
	 */
	class HomeRow extends Location {
		public HomeRow(Color color, int index) {
			super(new Color[] { color }, index);
		}
	}

	class Home extends Location {
		// Home has a Color (the player's color), and extends "past" the board.
		public Home(Color color, int index) {
			super(new Color[] { color }, index);
		}

		@Override
		public int next(Pawn p) throws OperationNotSupportedException {
			throw new OperationNotSupportedException(
				"Cannot call Home.next(...): There is no 'next' after Home."
			);
		}
	}

	class Neutral extends Location {
		public Neutral (int index) {
			super(new Color[] { Color.Neutral }, index);
		}
	}

	class Safe extends Location {
		public Safe(Color auxiliaryColor, int index) {
			super(new Color[] { auxiliaryColor, Color.Safe }, index);
		}

		public Safe(int index) {
			super(new Color[] { Color.Safe }, index);
		}
	}

	class Entry extends Safe {
		public Entry(Color color, int index) {
			super(color, index);
		}
	}

	class HomeEntry extends Safe {
		Color homeColor;

		public HomeEntry(Color homeColor, int index) {
			super(index);

			this.homeColor = homeColor;
		}

		@Override
		public int next(Pawn p) {
			if (p.color.equals(homeColor)) {
				// Enter the home row.
				return index + 1;
			} else {
				// Skip the home row.
				return index + spacesPerRow - 1;
			}
		}
	}

	// Board size constants.
	static final int spacesPerRow = 8;
	// NOTE: Must be odd (in order to be in middle).
	static final int rowsPerDimension = 3;
	// NOTE: If a different dimension is chosen, renderer will need to be refactored.
	static final int dimensions = 4;
	static final int size = spacesPerRow * rowsPerDimension * dimensions;

	/* dist(space s1, corresponding space s2 in the next dimension over) =
	 *   (# rows - 1 for the home row) * spaces per row + 1 for the home entry
	 */
	static final int dimensionDistance = spacesPerRow * rowsPerDimension;

	// For convenience during Board generation.
	static final int firstEntryIndex     = spacesPerRow / 2; // + 1 - 1
	static final int firstSafeIndex      = firstEntryIndex + spacesPerRow - 1;
	static final int firstHomeEntryIndex = firstSafeIndex + (spacesPerRow / 2) + 1;

	// Board spaces/locations.
	Location[] locations = new Location[size];
	// Pawns on the board.
	static final int pawnsPerPlayer = 4;
	Pawn[] pawns = new Pawn[pawnsPerPlayer * dimensions];
	// Pawn coordinates.
	HashMap<Pawn, Integer> pawnCoordinates = new HashMap<Pawn, Integer>();
	// Assigned player colors.
	HashMap<Player, Color> playerColors = new HashMap<Player, Color>();

	public Board () {
		/* NOTE: This assumes there is a Color.Player{1, 2, ... i} for 1 <= i <= dimensions.
		 * In other words: assumes 1 player per dimension and 1 color per player.
		 */
		int entryIndex = 0;

		// Starting in top-left-most space. (The space closest to HomeEntry on the side of Entry.)
		// And skipping the last 4 spaces -- the "out of bounds" Home spaces.
		int i;
		for (i = 0; i < size; i++) {
			// Entry.
			if ((i - firstEntryIndex) % dimensionDistance == 0) {
				entryIndex++;
				locations[i] = new Entry(Color.valueOf("Player" + entryIndex), i);
			// Plain Safe.
			} else if ((i - firstSafeIndex) % dimensionDistance == 0) {
				locations[i] = new Safe(i);
			// HomeEntry.
			} else if ((i - firstHomeEntryIndex) % dimensionDistance == 0) {
				locations[i] = new HomeEntry(Color.valueOf("Player" + entryIndex), i);
				// Insert the entire home row.
				int homeRowStart = i;
				for (i = homeRowStart + 1; i < homeRowStart + (spacesPerRow - 1); i++) {
					locations[i] = new HomeRow(Color.valueOf("Player" + entryIndex), i);
				}
				// Insert Home space.
				locations[i] = new Home(Color.valueOf("Player" + entryIndex), i);
			// o/w: Neutral.
			} else {
				locations[i] = new Neutral(i);
			}
		}
	}

	public Board performMove(Move m) {
		if (m instanceof parcheesi.move.EnterPiece) {
			// TODO
		}

		return new Board();
	}

	public Pawn[] getPlayerPawnsInStart(Player player) {
		ArrayList<Pawn> pawnsInStart = new ArrayList<Pawn>();
		// If a pawn has the same color as player, and has no coordinate, then it is in start.
		for (Pawn p : pawns) {
			if (p.getColor().equals(playerColors.get(player)) && !pawnCoordinates.containsKey(p)) {
				pawnsInStart.add(p);
			}
		}
		return pawnsInStart.toArray(new Pawn[pawnsInStart.size()]);
	}

	public static void main(String[] args) {
		new BoardTester();
	}

	static class BoardTester extends parcheesi.test.Tester {
		int countLocationsOfType (Class classObject, Board b) {
			int count = 0;
			for (int i = 0; i < Board.size; i++) {
				// System.out.println(i + " " + b.locations[i].getClass().getSimpleName());
				if (classObject.isInstance(b.locations[i])) count++;
			}
			return count;
		}

		public BoardTester() {
			Board newBoard = new Board();

			check(
				newBoard.locations.length == Board.size,
				"A Board should have Board.size locations."
			);

			int numEntries = countLocationsOfType(Entry.class, newBoard);
			int numHomeEntries = countLocationsOfType(HomeEntry.class, newBoard);
			int numHomeSpaces = countLocationsOfType(Home.class, newBoard);
			int numSafeSpaces = countLocationsOfType(Safe.class, newBoard);
			int numPlainSafeSpaces = numSafeSpaces - numEntries - numHomeEntries;

			check(
				numEntries == dimensions
					&& numHomeEntries == dimensions
					&& numPlainSafeSpaces == dimensions
					&& numHomeSpaces == dimensions
					&& numSafeSpaces == dimensions * 3,
				"Every dimension should have 3 Safe spaces: 1 Entry, 1 HomeEntry, and 1 plain Safe."
			);

			int numHomeRowSpaces = countLocationsOfType(HomeRow.class, newBoard);
			check(
				// spacesPerRow - 1 for Home, -1 for HomeEntry = spacesPerRow - 2
				numHomeRowSpaces == dimensions * (spacesPerRow - 2),
				"A board should have " + (spacesPerRow - 2) + " HomeRow spaces per dimension/player."
			);

			check(
				countLocationsOfType(Neutral.class, newBoard)
					== Board.size - (numHomeSpaces + numHomeRowSpaces + numSafeSpaces),
				"All remaining spaces should be Neutral."
			);

			int[] counts = new int[] {
				spacesPerRow / 2,
				1,
				spacesPerRow - 2,
				1
			};

			Class[] classes = new Class[] {
				Neutral.class,
				Entry.class,
				Neutral.class,
				Safe.class,
				Neutral.class,
				HomeEntry.class,
				HomeRow.class,
				Home.class
			};

			boolean orderIsCorrect = true;

			int traversed = 0;
			int iteration = 0;
			int repetitions, start;
			Class expected;
			for (; traversed < size; iteration++) {
				repetitions = counts[iteration % 4];
				start = traversed;
				for (; traversed < start + repetitions; traversed++) {
					expected = classes[iteration % 8];
					orderIsCorrect &= expected.isInstance(newBoard.locations[traversed]);
				}
			}

			check(orderIsCorrect, "Patterns in the board repeat as expected.");

			summarize();
		}
	}
}

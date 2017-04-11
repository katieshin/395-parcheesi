package parcheesi;

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

		public int next(Pawn p) throws UnsupportedOperationException {
			return index + 1;
		};
	}

	/* NOTE: Although HomeRow is technically Safe, it doesn't need to extend Safe, because a pawn of
	 * a color dissimilar to that of the HomeRow locations cannot enter the HomeRow and therfore no
	 * bopping can occur.
	 */
	class HomeRow extends Location {
		public HomeRow(Color color, int index) {
			super(new Color[] { color }, index);
		}
	}

	class Home extends Location {
		// Home has a Color (the player's color).
		public Home(Color color, int index) {
			super(new Color[] { color }, index);
		}

		@Override
		public int next(Pawn p) throws UnsupportedOperationException {
			throw new UnsupportedOperationException(
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
		public Color homeColor;

		public HomeEntry(Color homeColor, int index) {
			super(index);

			this.homeColor = homeColor;
		}

		@Override
		public int next(Pawn p) {
			if (p.getColor().equals(homeColor)) {
				// Enter the home row.
				return index + 1;
			} else {
				// Skip the home row.
				return index + spacesPerRow;
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
	 *  total # of spaces in a dimension, that is: dimension size.
	 */
	static final int dimensionSize = spacesPerRow * rowsPerDimension;

	// For convenience during Board generation.
	static final int firstEntryIndex     = spacesPerRow / 2; // + 1 - 1
	static final int firstSafeIndex      = firstEntryIndex + spacesPerRow - 1;
	static final int firstHomeEntryIndex = firstSafeIndex + (spacesPerRow / 2) + 1;

	// Unlikely that we'll want to change this, but let's use a variable so that we can.
	static final int pawnsPerPlayer = 4;

	// Board spaces/locations.
	Location[] locations = new Location[size];
	// Pawns on the board.
	Pawn[] pawns = new Pawn[pawnsPerPlayer * dimensions];
	int currentPawnIndex = 0;
	// Pawn coordinates.
	HashMap<Pawn, Integer> pawnCoordinates = new HashMap<Pawn, Integer>();
	// Assigned player colors.
	HashMap<Player, Color> playerColors = new HashMap<Player, Color>();

	public Board () {
		/* NOTE: Board generation assumes there is a Color.Player{1, 2, ... i} for
		 * 1 <= i <= dimensions. In other words: assumes 1 player per dimension and 1 color per
		 * player.
		 */
		int entryIndex = 0;

		/* Start in top-left-most space. (The space closest to HomeEntry on the side of Entry in
		 * Player 4's dimension/board-section.)
		 */
		int i;
		for (i = 0; i < size; i++) {
			// Entry.
			if ((i - firstEntryIndex) % dimensionSize == 0) {
				entryIndex++;
				locations[i] = new Entry(Color.valueOf("Player" + entryIndex), i);
			// Plain Safe.
			} else if ((i - firstSafeIndex) % dimensionSize == 0) {
				locations[i] = new Safe(i);
			// HomeEntry.
			} else if ((i - firstHomeEntryIndex) % dimensionSize == 0) {
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

	public void addPawn(Pawn p) throws IllegalStateException {
		if (currentPawnIndex < pawns.length) {
			pawns[currentPawnIndex++] = p;
		} else {
			throw new IllegalStateException(
				"Cannot have more than " + pawnsPerPlayer + " pawns per player,"
				+ " or " + (pawnsPerPlayer * dimensions) + " total."
			);
		}
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

	public static void main(String[] args) throws UnsupportedOperationException {
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

		public BoardTester() throws UnsupportedOperationException {
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
			int repetitions, start, expectedNext, next;
			Class expected, expectedNextClass;
			Location location;
			// Choose pawn color such that pawn must travel entire board.
			Pawn p = new Pawn(1, Color.Player4.getColorName());

			// FIXME Either there is a bug in HomeEntry.next, or this code is broken.
			for (; traversed < size; iteration++) {
				repetitions = counts[iteration % 4];
				expected = expectedNextClass = classes[iteration % 8];
				start = traversed;

				for (; traversed < start + repetitions; traversed++) {
					location = newBoard.locations[traversed];
					orderIsCorrect &= expected.isInstance(location);

					expectedNext = traversed + 1;

					if (location instanceof Home) {
						if (location.colors[0].equals(p.getColor())) {
							boolean fail = false;
							try {
								location.next(p);
								fail = true;
							} catch (UnsupportedOperationException ex) { }
							check(!fail, "Home.next(...) throws UnsupportedOperationException.");
						}
						continue;
					}

					if (location instanceof HomeRow) {
						if (!location.colors[0].equals(p.getColor())) {
							// The pawn can never reach this location.
							continue;
						} else if (traversed == (start + repetitions - 1)) {
							expectedNextClass = classes[(iteration + 1) % 8];
						}
					} else if (location instanceof HomeEntry) {
						// Special case for location.next(...).
						if (!((HomeEntry)location).homeColor.equals(p.getColor())) {
							// Skip all the HomeRows + Home.
							expectedNext += (spacesPerRow - 1);
							// Skip HomeRow.class, Home.class, go to 3rd next class.
							expectedNextClass = classes[(iteration + 3) % 8];
						} else {
							expectedNextClass = classes[(iteration + 1) % 8];
						}
					}  else if (traversed == (start + repetitions - 1)) {
						// If we are on the last repetition, look at the next class.
						expectedNextClass = classes[(iteration + 1) % 8];
					}

					next = location.next(p);
					check(
						next == expectedNext,
						"The 'next' index changes correctly @ " + traversed
						+ " to " + expectedNext + "."
					);

					check(
						expectedNextClass.isInstance(newBoard.locations[next]),
						"The 'next' Location is an instance of the correct class @ "
						+ expectedNext + ", that is: " + expectedNextClass.getSimpleName() + "."
					);
				}
			}

			check(orderIsCorrect, "Patterns in the board repeat as expected.");

			// TODO: test getPlayerPawnsInStart, performMove, ...

			summarize();
		}
	}
}

package parcheesi;

import java.util.ArrayList;
import java.util.HashMap;

import parcheesi.player.Player;
import parcheesi.move.Move;
import parcheesi.Color;

public class Board {
	abstract class Location {
		public Color dimensionColor;

		// NOTE: Colors should be drawn "bottom to top" (color 0 then color 1).
		Color[] displayColors = new Color[2];
		int index;

		public Location(int index) {
			this.index  = index;

			/* TODO?: Refactor
			 * This adjustment wouldn't be necessary if we had started counting at the first
			 * location in the dimension instead of at the top-left-most space... Oh well.
			 */
			int offsetIndex = (index - spacesPerRow) % Board.size;
			int playerIndex = offsetIndex / dimensionSize;

			this.dimensionColor = Color.valueOf("Player" + (playerIndex + 1));

			this.displayColors[0] = dimensionColor;
		}

		protected Location(int index, Color primaryDisplayColor) {
			this(index);
			this.displayColors[0] = primaryDisplayColor;
		}

		public int next(Pawn p) throws UnsupportedOperationException {
			return index + 1;
		};
	}

	class Neutral extends Location {
		public Neutral (int index) {
			super(index, Color.Neutral);
		}
	}

	class Safe extends Location {
		public Safe(int index) {
			super(index, Color.Safe);
		}
	}

	class Home extends Safe {
		public Home(int index) {
			super(index);
		}

		@Override
		public int next(Pawn p) throws UnsupportedOperationException {
			throw new UnsupportedOperationException(
				"Cannot call Home.next(...): There is no 'next' after Home."
			);
		}
	}

	class HomeRow extends Safe {
		public HomeRow(int index) {
			super(index);
		}
	}

	class Entry extends Safe {
		public Entry(int index) {
			super(index);
			this.displayColors[1] = dimensionColor;
		}
	}

	class HomeEntry extends Safe {
		public HomeEntry(int index) {
			super(index);
		}

		@Override
		public int next(Pawn p) {
			// FIXME: p.color may not be accessible if we refactor.
			if (Color.lookupByColorName(p.color).equals(dimensionColor)) {
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
	//										this doesn't work correctly for odd spacesPerRow
	// static final int firstSafeIndex      = spacesPerRow / 2 - 1; // + 1 - 1
	// static final int firstHomeEntryIndex = spacesPerRow;
	// static final int firstEntryIndex     = firstHomeEntryIndex + spacesPerRow / 2 + 1;
	static final int firstEntryIndex     = spacesPerRow / 2;
	static final int firstSafeIndex      = firstEntryIndex + spacesPerRow - 1;
	static final int firstHomeEntryIndex = firstSafeIndex + spacesPerRow / 2 + 1;

	// Unlikely that we'll want to change this, but let's use a variable so that we can.
	static final int pawnsPerPlayer = 4;

	// Board spaces/locations.
	Location[] locations = new Location[size];
	// Pawns on the board.
	Pawn[] pawns = new Pawn[pawnsPerPlayer * dimensions];
	HashMap<Integer, Color> pawnColors = new HashMap<Integer, Color>();
	// Pawn coordinates.
	HashMap<Integer, Integer> pawnCoordinates = new HashMap<Integer, Integer>();
	// Assigned player colors.
	Player[] players = new Player[dimensions];
	HashMap<Integer, Color> playerColors = new HashMap<Integer, Color>();

	public Board () throws UnsupportedOperationException {
		// Assign each player a color.
		for (int p = 0; p < players.length; p++) {
			Color playerColor = Color.valueOf("Player" + (p + 1));

			if (playerColor == null) {
				throw new IllegalStateException(
						"Cannot instantiate a Board with more Players than there are player colors."
					);
			}

			playerColors.put(p, playerColor);
		}

		// Create pawns for each player.
		for (int p = 0; p < players.length; p++) {
			Color color = playerColors.get(p);
			for (int pw = 0; pw < pawnsPerPlayer; pw++) {
				int pawnId = p * pawnsPerPlayer + pw;
				/* NOTE: accoring to Pawn comments + spec, id should be 0-3 and color should be
				 * String. This isn't ideal for our implementation, but we will work around it.
				 */
				pawns[pawnId] = new Pawn(pw, color.getColorName());
			}
		}

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
				locations[i] = new Entry(i);
			// Plain Safe.
			} else if ((i - firstSafeIndex) % dimensionSize == 0) {
				locations[i] = new Safe(i);
			// HomeEntry.
			} else if ((i - firstHomeEntryIndex) % dimensionSize == 0) {
				locations[i] = new HomeEntry(i);
				// Insert the entire home row.
				int homeRowStart = i;
				for (i = homeRowStart + 1; i < homeRowStart + (spacesPerRow - 1); i++) {
					locations[i] = new HomeRow(i);
				}
				// Insert Home space.
				locations[i] = new Home(i);
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
			/* FIXME: p.color is only accessible here because by happy accident Board and Pawn
			 * currently share the same package (parcheesi). They may not always if we do any
			 * refactoring at all ever, and so we should make it possible to access p.color via
			 * another method. (See PLAN.md for an idea.)
			 */
			if (Color.lookupByColorName(p.color).equals(playerColors.get(player))
					&& !pawnCoordinates.containsKey(p)) {
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
			int numHomeRowSpaces = countLocationsOfType(HomeRow.class, newBoard);
			int numSafeSpaces = countLocationsOfType(Safe.class, newBoard);
			int numPlainSafeSpaces = numSafeSpaces
				- (numEntries + numHomeEntries + numHomeRowSpaces + numHomeSpaces);

			check(
				numEntries == dimensions,
				"Every dimension should have 1 Entry"
			);
			check(
				numHomeEntries == dimensions,
				"Every dimension should have 1 HomeEntry"
			);
			check(
				numPlainSafeSpaces == dimensions,
				"Every dimension should have 1 plain Safe"
			);
			check(
				numHomeSpaces == dimensions,
				"Every dimension should have 1 Home"
			);
			check(
				// spacesPerRow - 1 for Home, -1 for HomeEntry = spacesPerRow - 2
				numHomeRowSpaces == dimensions * (spacesPerRow - 2),
				"Every dimension should have" + (spacesPerRow - 2) + " HomeRow spaces"
			);
			check(
				numSafeSpaces == dimensions * (4 + (spacesPerRow - 2)),
				"Every dimension should have " + (4 + (spacesPerRow - 2)) + " Safe spaces:"
				+ " 1 Entry, 1 HomeEntry, 1 plain Safe, 1 Home," + (spacesPerRow - 2) + " HomeRow spaces"
			);
			check(
				countLocationsOfType(Neutral.class, newBoard)
					== Board.size - numSafeSpaces,
				"All remaining (non-safe) spaces should be Neutral"
			);

			int[] counts = new int[] {
				spacesPerRow / 2, // Neutral
				1,				  // Entry or HomeEntry
				spacesPerRow - 2, // Neutral or HomeRow
				1				  // Safe or Home
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
			// Choose pawn color such that pawn must travel entire board it can possibly travel.
			Pawn p = new Pawn(0, Color.valueOf("Player" + dimensions).getColorName());

			for (; traversed < size; iteration++) {
				repetitions = counts[iteration % counts.length];
				expected = expectedNextClass = classes[iteration % classes.length];
				start = traversed;

				for (; traversed < start + repetitions; traversed++) {
					location = newBoard.locations[traversed];
					orderIsCorrect &= expected.isInstance(location);

					expectedNext = traversed + 1;

					if (location instanceof Home) {
						if (location.dimensionColor.equals(Color.lookupByColorName(p.color))) {
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
						if (!location.dimensionColor.equals(Color.lookupByColorName(p.color))) {
							// The pawn can never reach this location.
							continue;
						}
					}

					if (location instanceof HomeEntry
							&& !location.dimensionColor.equals(Color.lookupByColorName(p.color))) {
						// Skip all the HomeRows + Home.
						expectedNext += (spacesPerRow - 1);
						// Skip HomeRow.class, Home.class, go to 3rd next class.
						expectedNextClass = classes[(iteration + 3) % classes.length];
					}  else if (traversed == (start + repetitions - 1)) {
						// If we are on the last repetition, look at the next class.
						expectedNextClass = classes[(iteration + 1) % classes.length];
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

package parcheesi;

import java.util.ArrayList;
import java.util.HashMap;

import static parcheesi.Parameters.Board.*;
import static parcheesi.Parameters.pawnsPerPlayer;

import parcheesi.player.Player;
import parcheesi.move.Move;
import parcheesi.Color;

public class Board {
	abstract class Location {

		int index;
		public Color dimensionColor;

		// NOTE: Colors should be drawn "bottom to top" (color 0 then color 1).
		Color[] displayColors = new Color[2];

		public Location(int index) {
			this.index  = index;

			int dimension = this.index / dimensionSize;
			this.dimensionColor = Color.valueOf("Player" + (dimension + 1));

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
			Color playerColor = Color.forPlayer(p);

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

		// Start at the first space (counter clockwise) of the 1st player's dimension.
		int i;
		for (i = 0; i < size; i++) {
			// Plain Safe.
			if ((i - firstSafeIndex) % dimensionSize == 0) {
				locations[i] = new Safe(i);
			// HomeEntry.
			} else if ((i - firstHomeEntryIndex) % dimensionSize == 0) {
				locations[i] = new HomeEntry(i);
				// Insert Home row.
				int homeEntryIndex = i;
				for (i = homeEntryIndex + 1; i < homeEntryIndex + (spacesPerRow - 1); i++) {
					locations[i] = new HomeRow(i);
				}
				// Insert Home.
				locations[i] = new Home(i);
			// Entry.
			} else if ((i - firstEntryIndex) % dimensionSize == 0) {
				locations[i] = new Entry(i);
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
			for (int i = 0; i < size; i++) {
				// System.out.println(i + " " + b.locations[i].getClass().getSimpleName());
				if (classObject.isInstance(b.locations[i])) count++;
			}
			return count;
		}

		public BoardTester() throws UnsupportedOperationException {
			Board newBoard = new Board();

			check(
				newBoard.locations.length == size,
				"A Board should have Parameters.Board.size locations"
			);

			boolean noNullLocations = true;
			for (Location l : newBoard.locations) {
				noNullLocations &= (l != null);
			}
			check(
				noNullLocations,
				"No locations are null"
			);

			int numEntries         = countLocationsOfType(Entry.class, newBoard);
			int numHomeEntries     = countLocationsOfType(HomeEntry.class, newBoard);
			int numHomeSpaces      = countLocationsOfType(Home.class, newBoard);
			int numHomeRowSpaces   = countLocationsOfType(HomeRow.class, newBoard);
			int numSafeSpaces      = countLocationsOfType(Safe.class, newBoard);
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
				"Every dimension should have " + (spacesPerRow - 2) + " HomeRow spaces"
			);
			check(
				numSafeSpaces == dimensions * (4 + (spacesPerRow - 2)),
				"Every dimension should have " + (4 + (spacesPerRow - 2)) + " Safe spaces:"
				+ " 1 Entry, 1 HomeEntry, 1 plain Safe, 1 Home, " + (spacesPerRow - 2) + " HomeRow spaces"
			);
			check(
				countLocationsOfType(Neutral.class, newBoard)
					== size - numSafeSpaces,
				"All remaining (non-safe) spaces should be Neutral"
			);

			// NOTE: counts is symmetric around HomeRow. That's neat.
			int[] counts = new int[] {
				(spacesPerRow / 2) - 1, // Neutral
				1,                      // Safe
				spacesPerRow / 2,       // Neutral
				1,                      // HomeEntry
				spacesPerRow - 2,       // HomeRow
				1,                      // Home
				spacesPerRow / 2,       // Neutral
				1,                      // Entry
				(spacesPerRow / 2) - 1  // Neutral
			};

			Class[] classes = new Class[] {
				Neutral.class,
				Safe.class,
				Neutral.class,
				HomeEntry.class,
				HomeRow.class,
				Home.class,
				Neutral.class,
				Entry.class,
				Neutral.class
			};

			boolean orderIsCorrect = true;

			int traversed = 0;
			int iteration = 0;

			int repetitions, start, expectedNext, nextIndex;
			Color expectedColor;
			Class expectedClass, expectedNextClass;
			Location location, nextLocation;

			// Choose pawn color such that pawn must travel starting as close to 0 as possible.
			Pawn p = new Pawn(0, Color.forPlayer(0).getColorName());

			for (; traversed < size; iteration++) {
				repetitions   = counts[iteration % counts.length];
				expectedClass = classes[iteration % classes.length];
				expectedColor = Color.forPlayer(traversed / dimensionSize);
				start         = traversed;

				for (; traversed < start + repetitions; traversed++) {
					location = newBoard.locations[traversed];
					orderIsCorrect &= expectedClass.isInstance(location);

					check(
						orderIsCorrect,
						"Location @ " + traversed + " is of expected class " + expectedClass.getSimpleName()
					);

					check(
						location.dimensionColor.equals(expectedColor),
						"Location @ " + traversed + " is of expected color " + expectedColor.getColorName()
					);

					// If we are on a Home space, we cannot actually have any expectedNext[Class].
					if (location instanceof Home) {
						// Either we should throw an Error if we try to go next() on our own Home...
						if (location.dimensionColor.equals(Color.lookupByColorName(p.color))) {
							boolean fail = false;
							try {
								location.next(p);
								fail = true;
							} catch (UnsupportedOperationException ex) { }
							check(!fail, "Home.next(...) throws UnsupportedOperationException");
						}
						// Or we cannot be here at all; this is another player's home.
						continue;
					}

					// And if we are on a different player's HomeRow, ...
					if (location instanceof HomeRow) {
						if (!location.dimensionColor.equals(Color.lookupByColorName(p.color))) {
							// The pawn can never reach this location.
							continue;
						}
					}

					// Otherwise, we at first expect for "next" to be our current index + 1.
					expectedNext = traversed + 1;

					// If the next space is off the board, don't test it.
					if (expectedNext == size) {
						continue;
					}

					// We expect the class of the next space to be the same as the current class...
					expectedNextClass = expectedClass;
					// Unless we are on the last repetition, in which case it should be of the next class.
					if (traversed == (start + repetitions - 1)) {
						expectedNextClass = classes[(iteration + 1) % classes.length];
					}

					// If this is a HomeEntry, and not our HomeEntry, ...
					if (location instanceof HomeEntry
							&& !location.dimensionColor.equals(Color.lookupByColorName(p.color))) {
						// We expect next to come after this player's HomeRow + Home.
						expectedNext += (spacesPerRow - 1);
						// And it should be of the class coming after HomeRow.class and Home.class in sequence.
						expectedNextClass = classes[(iteration + 3) % classes.length];
					}

					// Now get the actual values.
					nextIndex = location.next(p);
					nextLocation = newBoard.locations[nextIndex];

					check(
						nextIndex == expectedNext,
						"The nextIndex changes correctly @ " + traversed + " to " + expectedNext
					);

					check(
						expectedNextClass.isInstance(nextLocation),
						"The nextLocation is an instance of the correct class @ " + expectedNext + ", that is: "
						+ expectedNextClass.getSimpleName()
					);
				}
			}

			check(orderIsCorrect, "Patterns in the board repeat as expected");

			// TODO: test getPlayerPawnsInStart, performMove, ...

			summarize();
		}
	}
}

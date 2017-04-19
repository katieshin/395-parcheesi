package parcheesi;

import java.util.ArrayList;
import java.util.HashMap;

import static parcheesi.Parameters.Board.*;
import static parcheesi.Parameters.pawnsPerPlayer;

import parcheesi.player.Player;
import parcheesi.move.Move;
import parcheesi.Color;

public class Board {
	// TODO: Refactor into parchessi.board.Board and parcheesi.board.Location.{...} (clean up).
	abstract class Location {

		int index;
		public Color dimensionColor;

		// NOTE: Colors should be drawn "bottom to top" (color 0 then color 1).
		Color[] displayColors = new Color[2];

		public Location(int index) {
			this.index  = index;

			int dimension = this.index / dimensionSize;
			this.dimensionColor = Color.forPlayer(dimension);

			this.displayColors[0] = dimensionColor;
		}

		protected Location(int index, Color primaryDisplayColor) {
			this(index);
			this.displayColors[0] = primaryDisplayColor;
		}

		public int next(Pawn p) throws UnsupportedOperationException {
			return (index + 1) % size;
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
	// Pawns on the board (coordinates).
	HashMap<Pawn, Integer> pawnCoordinates = new HashMap<Pawn, Integer>();
	// Assigned player colors.
	HashMap<Integer, Color> playerColors = new HashMap<Integer, Color>();

	public Board () throws IllegalStateException {
		// TODO: Currently we assume there will be as many players as dimensions. There could be fewer.
		// Assign each player a color.
		for (int p = 0; p < dimensions; p++) {
			Color playerColor = Color.forPlayer(p);

			if (playerColor == null) {
				throw new IllegalStateException(
						"Cannot instantiate a Board with more dimensions than there are player colors."
					);
			}

			playerColors.put(p, playerColor);
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

	public ArrayList<Integer> getPlayerPawnIndicesInStart(int playerIndex) {
		ArrayList<Integer> pawnsNotInStart = new ArrayList<Integer>();

		// If a pawn has the same color as player and has a coordinate, add to pawnsNotInStart.
		for (Pawn p : pawnCoordinates.keySet()) {
			/* FIXME: p.color and p.id are only accessible here because by happy accident Board and Pawn
			 * currently share the same package (parcheesi). They may not always if we do any refactoring
			 * at all ever, and so we should make it possible to access p.color via another method. (See
			 * PLAN.md for an idea.)
			 */
			if (Color.lookupByColorName(p.color).equals(playerColors.get(playerIndex))) {
				pawnsNotInStart.add(p.id);
			}
		}

		int numberOfPawnsInStart = pawnsPerPlayer - pawnsNotInStart.size();
		ArrayList<Integer> pawnsInStart = new ArrayList<Integer>(numberOfPawnsInStart);

		for (int i = 0; i < pawnsPerPlayer; i++) {
			if (!pawnsNotInStart.contains(i)) pawnsInStart.add(i);
		}

		return pawnsInStart;
	}

	private int getPlayerEntryIndex(int playerIndex) {
		return firstEntryIndex + (dimensionSize * playerIndex);
	}

	public int getPawnCoordinate(Pawn pawn) {
		if (!pawnCoordinates.containsKey(pawn)) {
			return -1;
		}

		return pawnCoordinates.get(pawn);
	}

	private void setPawnCoordinate(Pawn pawn, int coordinate) {
		pawnCoordinates.put(pawn, coordinate);
	}

	public boolean movePawnForward(Pawn pawn, int spaces) {
		int currentCoordinate = getPawnCoordinate(pawn);
		Location currentLocation = locations[currentCoordinate];

		for (int i = 0; i < spaces; i++) {
			if (currentLocation instanceof Home) {
				return false;
			}
			currentLocation = locations[currentLocation.next(pawn)];
		}

		setPawnCoordinate(pawn, currentLocation.index);

		return true;
	}

	public boolean addPawn(Pawn pawn) {
		if (pawnCoordinates.containsKey(pawn)) {
			return false;
		}

		int playerIndex = Color.lookupByColorName(pawn.color).ordinal();
		int playerEntryIndex = getPlayerEntryIndex(playerIndex);

		setPawnCoordinate(pawn, playerEntryIndex);

		return true;
	}

	public int pawnDistance(Pawn pawn) {
		// FIXME: reference to p.color may not always work
		int playerIndex  = Color.lookupByColorName(pawn.color).ordinal();
		int pawnStart    = getPlayerEntryIndex(playerIndex);
		int pawnEnd      = getPawnCoordinate(pawn);
		int distance = 0;

		// If you're not on the board or you're on entry, then distance is 0.
		if (pawnEnd == -1 || pawnEnd == pawnStart) {
			return distance;
		}

		// Otherwise, from the Entry...
		Location location = locations[pawnStart];
		// Until the index of your location is the same as the pawn's actual location index...
		while (location.index != pawnEnd) {
			// Move across the board, adding 1 to distance every time you move 1 space.
			distance++;
			location = locations[location.next(pawn)];
		}

		return distance;
	}

	public static void main(String[] args) throws UnsupportedOperationException {
		new BoardTester();
	}

	static class BoardTester extends parcheesi.test.Tester {
		public BoardTester() throws UnsupportedOperationException {
			boardGeneration();
			getPlayerEntryIndex();
			addPawn();
			playerPawnIndicesInStart();
			movePawnForward();
			pawnDistance();

			summarize();
		}

		int countLocationsOfType (Class classObject, Board b) {
			int count = 0;
			for (int i = 0; i < size; i++) {
				// System.out.println(i + " " + b.locations[i].getClass().getSimpleName());
				if (classObject.isInstance(b.locations[i])) count++;
			}
			return count;
		}

		void boardGeneration() {
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

			check(
				newBoard.playerColors.size() == dimensions,
				"Every player is assigned a color"
			);

			for (int i : newBoard.playerColors.keySet()) {
				Color expected = Color.forPlayer(i);
				String expectedName = expected.getColorName();
				check(
					newBoard.playerColors.get(i).equals(expected),
					"Player " + i + " is assigned the correct color for Player" + i + ", " + expectedName
				);
			}

			int numEntries         = countLocationsOfType(Entry.class, newBoard);
			int numHomeEntries     = countLocationsOfType(HomeEntry.class, newBoard);
			int numHomeSpaces      = countLocationsOfType(Home.class, newBoard);
			int numHomeRowSpaces   = countLocationsOfType(HomeRow.class, newBoard);
			int numSafeSpaces      = countLocationsOfType(Safe.class, newBoard);
			int numPlainSafeSpaces = numSafeSpaces
				- (numEntries + numHomeEntries + numHomeRowSpaces + numHomeSpaces);

			check(
				numEntries == dimensions,
				"There are " + dimensions + " Entry locations"
			);
			check(
				numHomeEntries == dimensions,
				"There are " + dimensions + " HomeEntry locations"
			);
			check(
				numPlainSafeSpaces == dimensions,
				"There are " + dimensions + " plain Safe locations"
			);
			check(
				numHomeSpaces == dimensions,
				"There are " + dimensions + " Home locations"
			);

			int expectedNumHomeRowSpaces = dimensions * (spacesPerRow - 2);
			check(
				// spacesPerRow - 1 for Home, -1 for HomeEntry = spacesPerRow - 2
				numHomeRowSpaces == expectedNumHomeRowSpaces,
				"There are " + expectedNumHomeRowSpaces + " HomeRow spaces"
			);

			int expectedNumSafeSpaces = dimensions * (4 + (spacesPerRow - 2));
			check(
				numSafeSpaces == expectedNumSafeSpaces,
				"There are " + expectedNumSafeSpaces + " Safe spaces:"
				+ " " + dimensions + " Entry, HomeEntry, plain Safe, & Home,"
				+ " " + expectedNumHomeRowSpaces + " HomeRow spaces"
			);

			check(
				countLocationsOfType(Neutral.class, newBoard) == size - numSafeSpaces,
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

			// Choosing this pawn color, pawn will enter Home on first iteration.
			Pawn pawn = new Pawn(0, Color.forPlayer(0).getColorName());

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
						if (location.dimensionColor.equals(Color.lookupByColorName(pawn.color))) {
							boolean fail = false;
							try {
								location.next(pawn);
								fail = true;
							} catch (UnsupportedOperationException ex) { }
							check(!fail, "Home.next(...) throws UnsupportedOperationException");
						}
						// Or we cannot be here at all; this is another player's home.
						continue;
					}

					// And if we are on a different player's HomeRow, ...
					if (location instanceof HomeRow) {
						if (!location.dimensionColor.equals(Color.lookupByColorName(pawn.color))) {
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
							&& !location.dimensionColor.equals(Color.lookupByColorName(pawn.color))) {
						// We expect next to come after this player's HomeRow + Home.
						expectedNext += (spacesPerRow - 1);
						// And it should be of the class coming after HomeRow.class and Home.class in sequence.
						expectedNextClass = classes[(iteration + 3) % classes.length];
					}

					// Now get the actual values.
					nextIndex = location.next(pawn);
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
		}

		void getPlayerEntryIndex() {
			Board board = new Board();

			int actualFirstEntryIndex = board.getPlayerEntryIndex(0);
			check(
				actualFirstEntryIndex == firstEntryIndex,
				"The entry index for the first player is at the first entry index: " + firstEntryIndex
			);

			boolean subsequentEntriesAreDimensionSizeApart = true;
			for (int i = 1; i < dimensions - 1; i++) {
				subsequentEntriesAreDimensionSizeApart &=
					(actualFirstEntryIndex + dimensionSize * i == board.getPlayerEntryIndex(i));
			}
			check(
				subsequentEntriesAreDimensionSizeApart,
				"Subsequent entries are all " + dimensionSize + " apart"
			);
		}

		void addPawn() {
			Board board = new Board();

			Pawn pawn = new Pawn(0, Color.forPlayer(0).getColorName());

			check(
				board.getPawnCoordinate(pawn) == -1,
				"The returned coordinate for a pawn not yet on the board is -1"
			);

			check(
				board.addPawn(pawn),
				"Adding Player 1's first pawn to the board succeeds"
			);

			check(
				board.getPawnCoordinate(pawn) == board.getPlayerEntryIndex(0),
				"A pawn added for Player 1 is placed in in Player 1's Entry"
			);

			check(
				!board.addPawn(pawn),
				"Trying to add Player 1's first pawn to the board a second time fails"
			);
		}

		void playerPawnIndicesInStart() {
			Board board = new Board();

			for (int i = 0; i < dimensions; i++) {
				check(
					board.getPlayerPawnIndicesInStart(0).size() == pawnsPerPlayer,
					"All pawns of a player who has added no pawns to the board will be in start ("
					+ (i + 1) + ")"
				);
			}

			Pawn pawn = new Pawn(0, Color.forPlayer(0).getColorName());

			board.addPawn(pawn);

			ArrayList<Integer> expectedPawnsInStart = new ArrayList<Integer>(pawnsPerPlayer - 1);
			for (int i = 1; i < pawnsPerPlayer; i++) expectedPawnsInStart.add(i);

			check(
				board.getPlayerPawnIndicesInStart(0).containsAll(expectedPawnsInStart),
				"If a player has entered only Pawn 0, then all other Pawn ids { 1, ... } are still in start"
			);

			Pawn pawn2 = new Pawn(1, Color.forPlayer(0).getColorName());
			Pawn pawn3 = new Pawn(2, Color.forPlayer(0).getColorName());
			Pawn pawn4 = new Pawn(3, Color.forPlayer(0).getColorName());

			board.addPawn(pawn2);
			board.addPawn(pawn3);
			board.addPawn(pawn4);

			check(
				board.getPlayerPawnIndicesInStart(0).size() == 0,
				"If a player has entered all their pawns, then no pawns are still in start"
			);
		}

		void movePawnForward() {
			Board board = new Board();
			Pawn pawn = new Pawn(0, Color.forPlayer(0).getColorName());
			board.addPawn(pawn);
			check(
				board.movePawnForward(pawn, 1),
				"Moving a pawn forward 1 from Entry should succeed"
			);

			// Count of spaces from player Entry to player HomeEntry
			int spacesToHomeEntry = spacesLeftAfterEntry
				+ mainRingSizePerDimension * (dimensions - 1)
				+ homeEntryIndexRelativeToDimensionStart;
			check(
				board.movePawnForward(pawn, spacesToHomeEntry),
				"Moving a pawn to its HomeEntry (" + spacesToHomeEntry + " spaces) should succeed"
			);

			check(
				!board.movePawnForward(pawn, (spacesPerRow - 1)),
				"Moving a pawn past Home should fail (1)"
			);

			check(
				board.movePawnForward(pawn, (spacesPerRow - 1) - 1),
				"Moving a pawn to Home should succeed"
			);

			check(
				!board.movePawnForward(pawn, 1),
				"Moving a pawn past Home should fail (2)"
			);
		}

		void pawnDistance() {
			Board board = new Board();
			Pawn pawn = new Pawn(0, Color.forPlayer(dimensions - 2).getColorName());

			check(
				board.pawnDistance(pawn) == 0,
				"If the pawn is not yet on the Board, its move distance is 0 moves"
			);

			check(
				board.addPawn(pawn) && board.pawnDistance(pawn) == 0,
				"If the pawn has just entered the Board, its move distance is 0 moves"
			);

			int distanceToLastEntry = mainRingSizePerDimension * (dimensions - 1);
			int maxPawnTravelDistance = distanceToLastEntry
				+ spacesLeftAfterEntry
				+ homeEntryIndexRelativeToDimensionStart;

			for (int i = 1; i < maxPawnTravelDistance; i++) {
				board.movePawnForward(pawn, 1);
				check(
					board.pawnDistance(pawn) == i,
					"If the pawn is moved " + i + " spaces, its distance is " + i
				);
			}
		}
	}
}

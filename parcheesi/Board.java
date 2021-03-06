package parcheesi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

import bugzapper.Contractual;
import bugzapper.Precondition;
import bugzapper.Postcondition;

import static parcheesi.Parameters.Board.*;
import static parcheesi.Parameters.pawnsPerPlayer;

import parcheesi.player.Player;
import parcheesi.pawn.Pawn;
import parcheesi.move.Move;
import parcheesi.Color;

@Contractual
public class Board {
	// TODO: Refactor into parchessi.board.Board and parcheesi.board.Location.{...} (clean up).
	abstract class Location {

		int index;
		public Color.Player dimensionColor;

		// NOTE: Colors should be drawn "bottom to top" (color 0 then color 1).
		Color.ColorEnum[] displayColors = new Color.ColorEnum[2];

		public Location(int index) {
			this.index  = index;

			int dimension = this.index / dimensionSize;
			this.dimensionColor = Color.forPlayer(dimension);

			this.displayColors[0] = dimensionColor;
		}

		protected Location(int index, Color.Space primaryDisplayColor) {
			this(index);
			this.displayColors[0] = primaryDisplayColor;
		}

		public int next(Pawn p) throws UnsupportedOperationException {
			return (index + 1) % size;
		};
	}

	class Neutral extends Location {
		public Neutral (int index) {
			super(index, Color.Space.Neutral);
		}
	}

	class Safe extends Location {
		public Safe(int index) {
			super(index, Color.Space.Safe);
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
			if (p.color.equals(dimensionColor)) {
				// Enter the home row.
				return index + 1;
			} else {
				// Skip the home row.
				return index + spacesPerRow + 1;
			}
		}
	}

	public boolean inNest(Pawn p) {
		return this.getPawnCoordinate(p) == -1;
	}

	public boolean inHome(Pawn p) {
		return !this.inNest(p) && locations[this.getPawnCoordinate(p)] instanceof Home;
	}

	public boolean inHomeRow(Pawn p) {
		return !this.inNest(p) && locations[this.getPawnCoordinate(p)] instanceof HomeRow;
	}

	public boolean inMain(Pawn p) {
		return !(this.inNest(p) || this.inHome(p) || this.inHomeRow(p));
	}

	public boolean inSafe(Pawn p) {
		return locations[this.getPawnCoordinate(p)] instanceof Safe;
	}

	// JavaScript strings are contracts now.
	// Arguments are automatically injected into the script engine instance
	// Parameters is autoinjected as a matter of convenience
	@Precondition(
		"coord >= 0"
		+ " && coord < Parameters.Board.size"
		+ " && self.getPawnsAtCoordinate(coord).length >= 1"
	)
	public boolean isBlockade(int coord) {
		/* NOTE: <= gives slightly more flexibility in the case that maximumPawnOccupancy is more than
		 * the pawnsToFormBlockade; it would make for a weird game of parcheesi, but sure.
		 */
		return pawnsToFormBlockade <= pawnCoordinates.entrySet()
			.stream()
			.filter((entry) -> entry.getValue() == coord)
			.count();
	}

	// Board spaces/locations.
	Location[] locations = new Location[size];
	// TODO:? Public locations mirror using Collections.unmodifiableList?
	// Pawns on the board (coordinates).
	HashMap<Pawn, Integer> pawnCoordinates = new HashMap<Pawn, Integer>();
	// Assigned player colors.
	HashMap<Integer, Color.Player> playerColors = new HashMap<Integer, Color.Player>();

	public Board() throws IllegalStateException {
		// TODO: Currently we assume there will be as many players as dimensions. There could be fewer.
		// Assign each player a color.
		for (int p = 0; p < dimensions; p++) {
			Color.Player playerColor = Color.forPlayer(p);

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
				for (i = homeEntryIndex + 1; i < homeEntryIndex + spacesPerRow; i++) {
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

	public Board(Board b) {
		locations = b.locations;
		playerColors = b.playerColors;
		for (Pawn p : b.pawnCoordinates.keySet()) {
			pawnCoordinates.put(p, b.pawnCoordinates.get(p));
		}
	}

	public boolean equals(Board other) {
		boolean equal = true;

		// NOTE: A Set of all pawns in either board
		Set<Pawn> pawns = new HashSet<Pawn>(pawnCoordinates.keySet());
		pawns.addAll(other.pawnCoordinates.keySet());

		for (Pawn p : pawns) {
			int otherCoord = other.getPawnCoordinate(p);
			equal &= (otherCoord == this.getPawnCoordinate(p));
		}

		return equal;
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

	@Postcondition(
		"self.getPawnsAtCoordinate(pawn).size() <= Parameters.Board.maximumPawnOccupancy"
	)
	public boolean movePawnForward(Pawn pawn, int spaces) {
		int currentCoordinate = getPawnCoordinate(pawn);

		if (currentCoordinate == -1) {
			return false;
		}

		Location currentLocation = locations[currentCoordinate];

		for (int i = 0; i < spaces; i++) {
			if (currentLocation instanceof Home) {
				return false;
			}

			int nextCoordinate = currentLocation.next(pawn);
			if (isBlockade(nextCoordinate)) {
				return false;
			}

			currentLocation = locations[nextCoordinate];
		}

		setPawnCoordinate(pawn, currentLocation.index);

		return true;
	}

	public boolean addPawn(Pawn pawn) {
		if (pawnCoordinates.containsKey(pawn)) {
			return false;
		}

		int playerIndex = pawn.playerIndex;
		int playerEntryIndex = getPlayerEntryIndex(playerIndex);

		if (isBlockade(playerEntryIndex)) {
			return false;
		}

		setPawnCoordinate(pawn, playerEntryIndex);

		return true;
	}

	public boolean removePawn(Pawn pawn) {
		if (!pawnCoordinates.containsKey(pawn)) {
			return false;
		}

		pawnCoordinates.remove(pawn);

		return true;
	}

	public int pawnDistance(Pawn pawn) {
		int playerIndex  = pawn.playerIndex;
		int pawnStart    = getPlayerEntryIndex(playerIndex);
		int pawnEnd      = getPawnCoordinate(pawn);
		int distance     = 0;

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

	// "result" is a special alias for the output after invoking the function
	@Postcondition(
		"result.size() <= Parameters.Board.maximumPawnOccupancy"
	)
	public List<Pawn> getPawnsAtCoordinate(int coord) {
		/* NOTE: this ArrayList will need to be resized if the board is currently invalid but that's
		 * okay. We will catch an error, if there is one, when we run the RulesChecker.
		 */
		List<Pawn> pawns = new ArrayList<Pawn>(maximumPawnOccupancy);

		for (Map.Entry<Pawn, Integer> coordinate : pawnCoordinates.entrySet()) {
			if (coordinate.getValue().equals(coord)) {
				// This pawn is in our location with us.
				pawns.add(coordinate.getKey());
			}
		}

		return pawns;
	}

	public static void main(String[] args) throws UnsupportedOperationException {
		new BoardTester();
	}

	static class BoardTester extends parcheesi.test.Tester {
		public BoardTester() throws UnsupportedOperationException {
			boardGeneration();
			getPlayerEntryIndex();
			addPawn();
			removePawn();
			movePawnForward();
			pawnDistance();
			boardEquality();
			getPawnsAtCoordinate();
			flagTests();

			summarize();
		}

		int countLocationsOfType(Class classObject, Board b) {
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
				"A Board should have " + size + " locations"
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
				Color.Player expected = Color.forPlayer(i);
				String expectedName = expected.getColorName();
				check(
					newBoard.playerColors.get(i).equals(expected),
					"Player " + i + " is assigned the correct color: Player" + i + " ( " + expectedName + " )"
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

			int expectedNumHomeRowSpaces = dimensions * (spacesPerRow - 1);
			check(
				// spacesPerRow - 1 for Home, -1 for HomeEntry = spacesPerRow - 2
				numHomeRowSpaces == expectedNumHomeRowSpaces,
				"There are " + expectedNumHomeRowSpaces + " HomeRow spaces"
			);

			int expectedNumSafeSpaces = dimensions * (4 + (spacesPerRow - 1));
			check(
				numSafeSpaces == expectedNumSafeSpaces,
				"There are " + expectedNumSafeSpaces + " Safe spaces:"
				+ " " + dimensions + " Entry, HomeEntry, plain Safe, & Home,"
				+ " " + expectedNumHomeRowSpaces + " HomeRow spaces"
			);

			check(
				countLocationsOfType(Neutral.class, newBoard) == size - numSafeSpaces,
				"All remaining " + (size - numSafeSpaces) + " (non-safe) spaces should be Neutral"
			);

			// NOTE: counts is symmetric around HomeRow. That's neat.
			int[] counts = new int[] {
				(spacesPerRow / 2) - 1, // Neutral
				1,                      // Safe
				spacesPerRow / 2,       // Neutral
				1,                      // HomeEntry
				spacesPerRow - 1,       // HomeRow
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
			Color.Player expectedColor;
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
						if (location.dimensionColor.equals(pawn.color)) {
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
						if (!location.dimensionColor.equals(pawn.color)) {
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
							&& !location.dimensionColor.equals(pawn.color)) {
						// We expect next to come after this player's HomeRow + Home.
						expectedNext += spacesPerRow;
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
						+ expectedNextClass.getSimpleName() + " == given: " + nextLocation.getClass().getSimpleName()
					);
				}
			}

			check(orderIsCorrect, "Patterns in the board repeat as expected");
		}

		void boardEquality() {
			Board b = new Board();
			Board copy = new Board(b);
			Pawn pawn = new Pawn(0, Color.forPlayer(0).getColorName());
			Pawn pawnCopy = new Pawn(0, Color.forPlayer(0).getColorName());

			check(
				b.equals(copy) && copy.equals(b),
				"An empty board and its copy are equal to one another"
			);

			copy.addPawn(pawn);
			check(
				!(b.equals(copy) || copy.equals(b)),
				"Boards with different numbers of pawns should not be equal"
			);

			b.addPawn(pawnCopy);
			check(
				!(b.equals(copy) || copy.equals(b)),
				"Boards with different instances of the same pawn with the same"
				+ " coordinate should not be equal"
			);

			b.removePawn(pawnCopy);
			b.addPawn(pawn);
			check(
				b.equals(copy) && copy.equals(b),
				"Boards with the same pawn in the same coordinate should be equal"
			);

			copy.movePawnForward(pawn, 1);
			check(
				!(b.equals(copy) || copy.equals(b)),
				"Boards with the same pawn in different coordinates should not be equal"
			);

			Pawn pawn2 = new Pawn(1, Color.forPlayer(0).getColorName());
			copy.addPawn(pawn2);
			check(
				!(b.equals(copy) || copy.equals(b)),
				"Boards with different pawns should not be equal"
			);

			b.addPawn(pawn2);
			check(
				!(b.equals(copy) || copy.equals(b)),
				"Boards with the same pawns but not the same coordinates should not be equal"
			);

			b.movePawnForward(pawn, 1);
			check(
				b.equals(copy) && copy.equals(b),
				"Boards with the same pawns in the same coordinates should be equal"
			);
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

		static final int removePawnIterations = 10;
		void removePawn() {
			Board board = new Board();
			Pawn pawn = new Pawn(0, Color.forPlayer(0).getColorName());

			check(
				!board.removePawn(pawn),
				"Removing a pawn not on the board fails"
			);

			boolean addSuccess = board.addPawn(pawn);
			check(
				addSuccess && board.removePawn(pawn),
				"Removing a pawn immediately after adding it succeeds"
			);

			java.util.Random rand = new java.util.Random();

			for (int i = 0; i < removePawnIterations; i++) {
				addSuccess = board.addPawn(pawn);
				int moveDistance = rand.nextInt(maxPawnTravelDistance - 1) + 1; // NOTE: Always move >= 1 spc.
				boolean moveSuccess = board.movePawnForward(pawn, moveDistance);

				check(
					addSuccess && moveSuccess && board.removePawn(pawn),
					"RANDOM TEST: A pawn can be removed from position " + (1 + moveDistance) + " on the board"
				);
			}
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
			int spacesToHomeEntry = (spacesLeftAfterEntry - 1)
				+ mainRingSizePerDimension * (dimensions - 1)
				+ homeEntryIndexRelativeToDimensionStart;
			check(
				board.movePawnForward(pawn, spacesToHomeEntry),
				"Moving a pawn to its HomeEntry (" + spacesToHomeEntry + " spaces) should succeed"
			);

			check(
				!board.movePawnForward(pawn, spacesPerRow + 1),
				"Moving a pawn past Home should fail (1)"
			);

			check(
				board.movePawnForward(pawn, spacesPerRow),
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

			/* NOTE: purposefully travel 1 space past the maxPawnTravelDistance to test that distance
			 * doesn't go up when you try to move past Home.
			 */
			for (int i = 1; i < maxPawnTravelDistance + 1; i++) {
				boolean moveSuccess = board.movePawnForward(pawn, 1);
				int distance = board.pawnDistance(pawn);
				check(
					// Went off the board:
					!moveSuccess && distance == (i - 1)
						// Still on the board:
						|| distance == i,
					"If the pawn is moved " + i + " spaces, its distance is "
					+ (moveSuccess ? i : "still " + (i - 1))
				);
			}
		}

		void getPawnsAtCoordinate() {
			Board board = new Board();
			Pawn pawn = new Pawn(0, Color.forPlayer(0).getColorName());
			int coord = board.getPlayerEntryIndex(0);
			List<Pawn> pawns = board.getPawnsAtCoordinate(coord);

			// Board is initially empty.
			boolean wholeBoardIsEmpty = true;
			for (Location location : board.locations) {
				pawns = board.getPawnsAtCoordinate(location.index);
				wholeBoardIsEmpty &= pawns.isEmpty();
			}

			check(
				wholeBoardIsEmpty,
				"Every board location should be empty on a new board"
			);

			// A single pawn enters the board.
			board.addPawn(pawn);
			coord = board.getPawnCoordinate(pawn);
			pawns = board.getPawnsAtCoordinate(coord);

			check(
				pawns.size() == 1 && pawns.contains(pawn),
				"After adding a pawn to the board, there should be one pawn at that pawn coordinate"
			);

			// A different color pawn enters the board.
			Pawn otherPlayerPawn = new Pawn(0, Color.forPlayer(1).getColorName());
			board.addPawn(otherPlayerPawn);

			int otherPlayerCoord = board.getPlayerEntryIndex(1);
			List<Pawn> otherPlayerPawns = board.getPawnsAtCoordinate(otherPlayerCoord);

			check(
				otherPlayerPawns.size() == 1 && otherPlayerPawns.contains(otherPlayerPawn)
				&& pawns.size() == 1 && pawns.contains(pawn),
				"After adding a pawn of a different color to the board, there should be one pawn at that"
				+ " pawn coordinate"
			);

			// A second pawn of the original color enters the board.
			Pawn sameColorPawn = new Pawn(1, Color.forPlayer(0).getColorName());
			board.addPawn(sameColorPawn);
			pawns = board.getPawnsAtCoordinate(coord);

			check(
				pawns.size() == 2 && pawns.contains(pawn) && pawns.contains(sameColorPawn),
				"After adding a new pawn of the same color, there are two pawns at that pawn coordinate"
			);

			// Move both the pawns of the original color to a different location.
			board.movePawnForward(pawn, 3);
			board.movePawnForward(sameColorPawn, 3);
			coord = board.getPawnCoordinate(pawn);
			pawns = board.getPawnsAtCoordinate(coord);

			check(
				pawns.size() == 2 && pawns.contains(pawn) && pawns.contains(sameColorPawn),
				"After moving two pawns of the same color separately to the same location, there are still"
				+ " two pawns at that new pawn coordinate"
			);
		}

		void flagTests() {
			Board board = new Board();
			Pawn p = new Pawn(0, Color.forPlayer(0));

			check(
				board.inNest(p),
				"A pawn that isn't on the board is in the nest"
			);

			board.addPawn(p);
			Pawn p2 = new Pawn(1, Color.forPlayer(0));
			board.addPawn(p2);

			check(
				board.isBlockade(board.getPawnCoordinate(p)),
				"A coordinate with 2 pawns on it is a blockade"
			);

			check(
				board.inMain(p),
				"A pawn that has entered the board is in main"
			);

			check(
				board.inSafe(p),
				"A pawn that is on a safe space is in safe"
			);

			board.movePawnForward(p, maxPawnTravelDistance);

			check(
				board.inHome(p),
				"A pawn that has reached home is in home"
			);
		}
	}
}

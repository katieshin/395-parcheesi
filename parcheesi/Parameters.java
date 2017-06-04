package parcheesi;

public class Parameters {
	public static final int maxPlayers = 4;

	// Unlikely that we'll want to change this, but let's use a variable so that we can.
	public static final int pawnsPerPlayer = 4;

	// The die value required to enter a pawn.
	public static final int dieValueToEnter = 5;

	// Board layout/size parameters.
	public class Board {
		// Corresponds to number of players that can exist on the board.
		public static final int dimensions = maxPlayers;
		// NOTE: If dimension changes, renderer will need to be able to handle it.

		// Number of pawns in a blockade.
		public static final int pawnsToFormBlockade = 2;

		// How many pawns can share a single location?
		public static final int maximumPawnOccupancy = 2;

		// Cannot be odd (our later calculations will break).
		public static final int spacesPerRow = 8;

		// Must be odd (in order to have a middle row for the home row).
		// FIXME: Cannot actually adjust this value because *pattern* tests don't account for it.
		public static final int rowsPerDimension = 3;

		/* Corresponds to the distance between two nodes of alike type and same relative
		 * position in adjacent dimensions.
		 *   e.g.: dist(Entry in dimension 2, Entry in dimension 1) = dimensionSize
		 */
		public static final int dimensionSize = spacesPerRow * rowsPerDimension;

		/* Corresponds to the distance between two nodes of alike type in two different dimensions,
		 * not counting the home row (i.e.; main ring distance, so the distance pawns travel).
		 *   e.g.: dist(Entry 1, Entry 2) for Player 1 Pawn = mainRingSizePerDimension
		 */
		public static final int mainRingSizePerDimension = dimensionSize - (spacesPerRow - 1);

		public static final int size = dimensionSize * dimensions;

		// These are used as a convenience during Board generation.
		// FIXME?: These calculations don't work correctly if spacesPerRow is odd.
		// FIXME?: These calculations don't work correctly if rowsPerDimension is even.
		public static final int firstSafeIndex      = spacesPerRow / 2 - 1;
		public static final int firstHomeEntryIndex = spacesPerRow;
		public static final int firstEntryIndex     = firstHomeEntryIndex + spacesPerRow + (spacesPerRow / 2);

		// Convenient aliases that have more meaning.
		public static final int safeIndexRelativeToDimensionStart = firstSafeIndex;
		public static final int homeEntryIndexRelativeToDimensionStart = firstHomeEntryIndex;
		public static final int entryIndexRelativeToDimensionStart = firstEntryIndex;
		public static final int spacesLeftAfterEntry = dimensionSize - entryIndexRelativeToDimensionStart;

		public static final int pawnMainRingDistance
			= mainRingSizePerDimension * (dimensions - 1)
			+ spacesLeftAfterEntry
			+ homeEntryIndexRelativeToDimensionStart;

		public static final int maxPawnTravelDistance
			= pawnMainRingDistance
				+ spacesPerRow; // NOTE: All other terms are obvious; this is Home Row thru to Home.
	}
}

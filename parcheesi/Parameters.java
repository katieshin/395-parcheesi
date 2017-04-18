package parcheesi;

class Parameters {
	// Board layout/size parameters.
	class Board {
		// Corresponds to number of players that can exist on the board.
		static final int dimensions = 4;
		// NOTE: If a different dimension than 4 is chosen, renderer will need to be able to handle it.

		// Cannot be odd (our later calculations will break).
		static final int spacesPerRow = 8;

		// Must be odd (in order to have a middle row for the home row).
		static final int rowsPerDimension = 3;

		/* Corresponds to the distance between two nodes of alike type and same relative
		 * position in adjacent dimensions.
		 *   e.g.: dist(Entry in dimension 2, Entry in dimension 1) = dimensionSize
		 */
		static final int dimensionSize = spacesPerRow * rowsPerDimension;

		/* Corresponds to the distance between two nodes of alike type in two different dimensions,
		 * not counting the home row (i.e.; main ring distance, so the distance pawns travel).
		 *   e.g.: dist(Entry 1, Entry 2) for Player 1 Pawn = mainRingSizePerDimension
		 */
		static final int mainRingSizePerDimension = dimensionSize - (spacesPerRow - 1);

		static final int size = dimensionSize * dimensions;

		// These are used as a convenience during Board generation.
		// FIXME?: These calculations don't work correctly if spacesPerRow is odd.
		// FIXME?: These calculations don't work correctly if rowsPerDimension is even.
		static final int firstSafeIndex      = spacesPerRow / 2 - 1;
		static final int firstHomeEntryIndex = spacesPerRow;
		static final int firstEntryIndex     = firstHomeEntryIndex + spacesPerRow + (spacesPerRow / 2);

		// Convenient aliases that have more meaning.
		static final int safeIndexRelativeToDimensionStart = firstSafeIndex;
		static final int homeEntryIndexRelativeToDimensionStart = firstHomeEntryIndex;
		static final int entryIndexRelativeToDimensionStart = firstEntryIndex;
	}

	// Unlikely that we'll want to change this, but let's use a variable so that we can.
	static final int pawnsPerPlayer = 4;
}

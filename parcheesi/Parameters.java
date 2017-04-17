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

		/* Corresponds to the distance between two nodes of alike type and relative
		 * position in adjacent dimensions.
		 *   e.g.: dist(Entry in dimension 2, Entry in dimension 1) = dimensionSize
		 */
		static final int dimensionSize = spacesPerRow * rowsPerDimension;

		static final int size = dimensionSize * dimensions;

		// These are used as a convenience during Board generation.
		// FIXME?: These calculations don't work correctly if spacesPerRow is odd.
		// FIXME?: These calculations don't work correctly if rowsPerDimension is even.
		static final int firstSafeIndex      = spacesPerRow / 2 - 1;
		static final int firstHomeEntryIndex = spacesPerRow;
		static final int firstEntryIndex     = firstHomeEntryIndex + spacesPerRow + (spacesPerRow / 2);
	}

	// Unlikely that we'll want to change this, but let's use a variable so that we can.
	static final int pawnsPerPlayer = 4;
}

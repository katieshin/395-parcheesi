package parcheesi.move;

/* NOTE: This would need to be updated with any new Moves if any new Moves were to be created.
 * TranslatedMove depends on its accuracy.
 */
// TODO: Use Java's crazy-ass dynamic classloader stuff to generate this data automagically.
public class Manifest {
	public static Class<? extends Move> MOVE_CLASSES = {
		MoveMain.class,
		MoveHome.class,
		EnterPiece.class
	}
}

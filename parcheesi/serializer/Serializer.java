package parcheesi.serializer;

import parcheesi.pawn.Pawn;
import parcheesi.die.Die;
import parcheesi.Board;

public interface Serializer <T> {
	public T serialize(Pawn[] pawns, Board board);
	public T serialize(Die[] dice);
}

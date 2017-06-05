package parcheesi.serializer.xml;

public class Element {
	public static Node<String> StartGame() { return new Node<String>("start-game"); }
	public static Node<String> Name()      { return new Node<String>("name"); }

	public static Node<Node> DoMove() { return new Node<Node>("do-move"); }
	public static Node<Node> Board()  { return new Node<Node>("board"); }
	public static Node<Node> Dice()   { return new Node<Node>("dice"); }

	public static Node.Empty DoublesPenalty() { return new Node.Empty("doubles-penalty"); }
	public static Node.Empty Void()           { return new Node.Empty("void"); }

	public static Node<Node> Start()    { return new Node<Node>("start"); }
	public static Node<Node> Home()     { return new Node<Node>("home"); }
	public static Node<Node> Main()     { return new Node<Node>("main"); }
	public static Node<Node> HomeRows() { return new Node<Node>("home-rows"); }
	public static Node<Node> PieceLoc() { return new Node<Node>("piece-loc"); }
	public static Node<Node> Pawn()     { return new Node<Node>("pawn"); }

	public static Node<Integer> Die()  { return new Node<Integer>("die"); }
	public static Node<Integer> Loc()  { return new Node<Integer>("loc"); }
	public static Node<String> Color() { return new Node<String>("color"); }
	public static Node<Integer> Id()   { return new Node<Integer>("id"); }
}

package parcheesi.serializer.xml;

class Element {
	static Node<String> StartGame() { return new Node<String>("start-game"); }
	static Node<String> Name()      { return new Node<String>("name"); }

	static Node<Node> DoMove() { return new Node<Node>("do-move"); }
	static Node<Node> Board()  { return new Node<Node>("board"); }
	static Node<Node> Dice()   { return new Node<Node>("dice"); }

	static Node.Empty DoublesPenalty() { return new Node.Empty("doubles-penalty"); }
	static Node.Empty Void()           { return new Node.Empty("void"); }

	static Node<Node> Start()    { return new Node<Node>("start"); }
	static Node<Node> Home()     { return new Node<Node>("home"); }
	static Node<Node> Main()     { return new Node<Node>("main"); }
	static Node<Node> HomeRows() { return new Node<Node>("home-rows"); }
	static Node<Node> PieceLoc() { return new Node<Node>("piece-loc"); }
	static Node<Node> Pawn()     { return new Node<Node>("pawn"); }

	static Node<Integer> Die()  { return new Node<Integer>("die"); }
	static Node<Integer> Loc()  { return new Node<Integer>("loc"); }
	static Node<String> Color() { return new Node<String>("color"); }
	static Node<Integer> Id()   { return new Node<Integer>("id"); }
}
